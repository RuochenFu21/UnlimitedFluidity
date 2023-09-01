package com.forsteri.unlimitedfluidity.core.flowinggas;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.HydrateBehavior;
import com.forsteri.unlimitedfluidity.util.Api;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@Api
public abstract class FlowingGas extends BehaviorableFluid {
    public static final int MAX_DENSITY = 128;
    public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_DENSITY);
    public static final Map<LevelAccessor, Map<BlockPos, Direction>> gasMovementMap = new HashMap<>();

    protected final Direction flowDirection;
    protected final double risePossibility;

    protected FlowingGas(Properties properties) {
        super(properties);

        this.flowDirection = properties.gasFlowDirection.direction;
        this.risePossibility = properties.risePossibility;

        if (this.isSource(this.defaultFluidState())) {
            this.registerDefaultState(this.getStateDefinition().any()
                    .setValue(DENSITY, MAX_DENSITY)
            );
        } else {
            this.registerDefaultState(this.getStateDefinition().any()
                    .setValue(DENSITY, MAX_DENSITY - 1)
                    .setValue(LEVEL, 7)
            );
        }
    }

    @Override
    public void tick(Level worldIn, BlockPos pos, FluidState state) {
        super.tick(worldIn, pos, state);

        if (worldIn.isClientSide) return;

        this.spread(worldIn, pos, state);
    }

    public GasMovementHandler getMovementHandler(LevelAccessor level) {
        return GasMovementHandler.getOrCreate(level, this);
    }

    public AbstractBaseGraph<BlockPos, DefaultEdge> getGraph(LevelAccessor level) {
        return getMovementHandler(level).getGraph();
    }

    @Override
    protected void spread(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        if (pState.isEmpty()) return;

        boolean rises = Math.random() < risePossibility;

        if (!rises && spreadVertically(pLevel, pPos, pState, true))
            spreadVertically(pLevel, pPos, pState, false);
        else if (moveInPath(pLevel, pPos, pState, true))
            moveInPath(pLevel, pPos, pState, false);
        else if (spreadHorizontally(pLevel, pPos, true))
            spreadHorizontally(pLevel, pPos, false);
        else if (rises && spreadVertically(pLevel, pPos, pState, true))
            spreadVertically(pLevel, pPos, pState, false);
        else if (findAndMoveUp(pLevel, pPos, pState, true))
            findAndMoveUp(pLevel, pPos, pState, false);

        getMovementHandler(pLevel).tick(pLevel.getLevelData().getDayTime());
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    protected void randomTick(Level pLevel, BlockPos pPos, FluidState pState, Random pRandom) {
        spread(pLevel, pPos, pState);
    }

    protected static Map<BlockPos, Direction> getGasMap(LevelAccessor level) {
        return gasMovementMap.computeIfAbsent(level, k -> new HashMap<>());
    }

    @SuppressWarnings("ConstantValue")
    protected boolean moveInPath(LevelAccessor pLevel, BlockPos pPos, FluidState pState, boolean simulated) {
        Direction direction = getGasMap(pLevel).get(pPos);
        if (direction == null) return false;

        int density = getMovementHandler(pLevel).getDensity(pPos.relative(direction));

        boolean invalid = false;
        boolean currentOnly = false;
        boolean fixableWithFindingAgain = false;

        invalid |= density == MAX_DENSITY;
        invalid |= looped(pLevel, pPos.relative(direction));
        invalid |= density == -1;
        invalid |= !getGasMap(pLevel).containsKey(pPos.relative(direction)) && direction != flowDirection;

        currentOnly |= density == MAX_DENSITY;
        fixableWithFindingAgain |= looped(pLevel, pPos);

        if (invalid) {
            getGasMap(pLevel).remove(pPos);
            if (!currentOnly)
                getGraph(pLevel).removeEdge(pPos, pPos.relative(direction));
            if (fixableWithFindingAgain && findAndMoveUp(pLevel, pPos, pState, true) && !simulated)
                findAndMoveUp(pLevel, pPos, pState, false);

            return false;
        }

        if (!simulated)
            getMovementHandler(pLevel).move(pPos, pState.getValue(DENSITY), direction);

        return true;
    }

    private static final List<BlockPos> loopCheck = new ArrayList<>();
    protected boolean looped(LevelAccessor pLevel, BlockPos pPos) {
        Direction gasMovement = getGasMap(pLevel).get(pPos);
        if (loopCheck.contains(pPos) || gasMovement == null || gasMovement == flowDirection) {
            boolean contains = loopCheck.contains(pPos);
            loopCheck.clear();
            return contains;
        }
        loopCheck.add(pPos);
        return looped(pLevel, pPos.relative(gasMovement));
    }

    protected boolean findAndMoveUp(LevelAccessor pLevel, BlockPos pPos, FluidState pState, boolean simulated) {
        if (!(getGraph(pLevel).clone() instanceof AbstractBaseGraph<?, ?> copied))
            throw new IllegalStateException("Graph is not a AbstractBaseGraph!");

        @SuppressWarnings("unchecked")
        AbstractBaseGraph<BlockPos, DefaultEdge> graph = (AbstractBaseGraph<BlockPos, DefaultEdge>) copied;

        boolean hasDestination = false;
        BlockPos destination = null;

        GraphProcessing: while (!graph.vertexSet().isEmpty()) {
            if (!graph.containsVertex(pPos)) return false;

            Iterator<BlockPos> iterator = new BreadthFirstIterator<>(graph, pPos);
            while (iterator.hasNext()) {
                destination = iterator.next();
                if (destination.getY() < pPos.getY()) {
                    graph.removeVertex(destination);
                    continue GraphProcessing;
                }

                if (destination.getY() == pPos.getY() + 1) {
                    hasDestination = true;
                    break GraphProcessing;
                }
            }

            return false;
        }

        if (!hasDestination) return false;

        BFSShortestPath<BlockPos, DefaultEdge> pathFinder = new BFSShortestPath<>(graph);

        GraphPath<BlockPos, DefaultEdge> path = pathFinder.getPath(pPos, destination);

        if (path.getVertexList().size() <= 1) return false;

        getGasMap(pLevel).putAll(
                IntStream.range(0, path.getVertexList().size() - 1).mapToObj(
                        i -> {
                            BlockPos from = path.getVertexList().get(i);
                            BlockPos to = path.getVertexList().get(i + 1);
                            return new Pair<>(from, to);
                        }
                ).collect(
                        Collectors.toMap(
                                Pair::getFirst,
                                pair -> Direction.getNearest(
                                        pair.getSecond().getX() - pair.getFirst().getX(),
                                        pair.getSecond().getY() - pair.getFirst().getY(),
                                        pair.getSecond().getZ() - pair.getFirst().getZ()
                                )
                        )
                )
        );

        if (!simulated) moveInPath(pLevel, pPos, pState, true);

        return true;
    }

    protected boolean spreadVertically(LevelAccessor pLevel, BlockPos pPos, FluidState pState, boolean simulate) {
        if (!canSpreadTo(pLevel, pPos.relative(flowDirection))) return false;
        if (pState.getValue(FlowingGas.DENSITY) > MAX_DENSITY - getMovementHandler(pLevel).getDensity(pPos.relative(flowDirection))) return false;

        if (simulate) return true;

        if (!pLevel.getBlockState(pPos.relative(flowDirection)).isAir())
            beforeDestroyingBlock(pLevel, pPos.relative(flowDirection), pLevel.getBlockState(pPos.relative(flowDirection)));

        getMovementHandler(pLevel).move(pPos, Math.min(pState.getValue(FlowingGas.DENSITY), MAX_DENSITY - getMovementHandler(pLevel).getDensity(pPos.relative(flowDirection))), flowDirection);
        for (BlockPos pos : new BlockPos[] {pPos, pPos.relative(flowDirection)})
            if (!getGraph(pLevel).containsVertex(pos))
                getGraph(pLevel).addVertex(pos);
        if (!getGraph(pLevel).containsEdge(pPos, pPos.relative(flowDirection)))
            getGraph(pLevel).addEdge(pPos, pPos.relative(flowDirection));

        return true;
    }

    protected boolean spreadHorizontally(LevelAccessor pLevel, BlockPos pPos, boolean simulated) {
        Graph<BlockPos, DefaultEdge> graph = getGraph(pLevel);
        int totalDensity = getMovementHandler(pLevel).getDensity(pPos);
        int space = 1;
        HashMap<BlockPos, Integer> addedAmountMap = new HashMap<>();

        for (Direction direction : Direction.Plane.HORIZONTAL)
            if (canSpreadTo(pLevel, pPos.relative(direction))) {
                BlockPos newPos = pPos.relative(direction);

                space++;
                totalDensity += getMovementHandler(pLevel).getDensity(newPos);
                addedAmountMap.put(newPos, 0);

                for (BlockPos pos : new BlockPos[] {pPos, newPos})
                    if (!graph.containsVertex(pos))
                        graph.addVertex(pos);

                if (!graph.containsEdge(pPos, newPos))
                    graph.addEdge(pPos, newPos);

                for (Direction direction2 : Direction.Plane.HORIZONTAL){
                    if (newPos.relative(direction2).equals(pPos))
                        continue;
                    if (addedAmountMap.containsKey(newPos.relative(direction2)))
                        continue;
                    if (canSpreadTo(pLevel, newPos.relative(direction2))) {
                        space++;
                        totalDensity += getMovementHandler(pLevel).getDensity(newPos.relative(direction2));
                        addedAmountMap.put(newPos.relative(direction2), 0);

                        for (BlockPos pos : new BlockPos[] {newPos, newPos.relative(direction2)})
                            if (!graph.containsVertex(pos))
                                graph.addVertex(pos);

                        if (!graph.containsEdge(newPos, newPos.relative(direction2)))
                            graph.addEdge(newPos, newPos.relative(direction2));
                    } else {
                        if (graph.containsEdge(newPos, newPos.relative(direction2)))
                            graph.removeEdge(newPos, newPos.relative(direction2));

                        for (BlockPos pos : new BlockPos[] {newPos, newPos.relative(direction2)})
                            if (graph.containsVertex(pos) && graph.edgesOf(pos).isEmpty())
                                graph.removeVertex(pos);

                    }
                }
            } else {
                if (graph.containsEdge(pPos, pPos.relative(direction)))
                    graph.removeEdge(pPos, pPos.relative(direction));

                for (BlockPos pos : new BlockPos[] {pPos, pPos.relative(direction)})
                    if (graph.containsVertex(pos) && graph.edgesOf(pos).isEmpty())
                        graph.removeVertex(pos);

            }

        if (space == 1) return false;

        int each, remainder;
        each = totalDensity / space;
        remainder = totalDensity % space;

        if (each == 0) return false;

        addedAmountMap.forEach((pos, amount) ->
                addedAmountMap.put(pos, each - getMovementHandler(pLevel).getDensity(pos))
        );

        if (remainder != 0) {
            @SuppressWarnings("unchecked")
            Map<BlockPos, Integer> copiedMap = ((HashMap<BlockPos, Integer>) addedAmountMap.clone());

            Set<BlockPos> remainderDirections = copiedMap.keySet();

            while (true) {
                Optional<Integer> optionalMin = copiedMap.values().stream().min(Integer::compareTo);

                if (optionalMin.isEmpty()) return false;

                Integer min = optionalMin.get();

                //noinspection OptionalGetWithoutIsPresent
                remainderDirections.remove(copiedMap.entrySet().stream().filter(entry -> entry.getValue().equals(min)).findFirst().get().getKey());

                if (remainderDirections.isEmpty()) return false;

                int remainderOfRemainder = remainder % remainderDirections.size();

                if (remainderOfRemainder == 0)
                    break;
            }

            remainderDirections.forEach(direction ->
                    addedAmountMap.put(direction, addedAmountMap.get(direction) + 1)
            );
        }

        addedAmountMap.values().removeIf(amount -> amount == 0);

        if (addedAmountMap.isEmpty()) return false;

        if (simulated) return true;

        for (Map.Entry<BlockPos, Integer> entry : addedAmountMap.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!pLevel.getBlockState(pos).isAir())
                beforeDestroyingBlock(pLevel, pos, pLevel.getBlockState(pos));

            getMovementHandler(pLevel).operations.add(GasMovementHandler.GasMovement.create()
                    .decrease(pPos, entry.getValue())
                    .increase(entry.getKey(), entry.getValue()));
        }

        return true;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        if (super.createLegacyBlock(state).getBlock() instanceof AirBlock) return super.createLegacyBlock(state);

        return super.createLegacyBlock(state).setValue(DENSITY, state.getValue(DENSITY));
    }

    protected boolean canSpreadTo(LevelAccessor pLevel, BlockPos pPos){
        return getMovementHandler(pLevel).getDensity(pPos) != -1;
    }

    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(DENSITY);
    }

    public @NotNull FluidState getFlowing(int p_75954_, boolean p_75955_) {
        return this.getFlowing().defaultFluidState().setValue(DENSITY, p_75954_).setValue(FALLING, p_75955_);
    }

    @Override
    public int getAmount(FluidState pState) {
        return pState.hasProperty(DENSITY) ? pState.getValue(DENSITY) : 0;
    }

    @Override
    protected void animateTick(Level p_76116_, BlockPos pPos, FluidState p_76118_, Random p_76119_) {
        if (!(p_76118_.getType() instanceof FlowingGas gas))
            return;

        render(p_76116_, pPos, gas, 3, 160);
    }

    public void render(BlockAndTintGetter pLevel, BlockPos pPos, FlowingGas gas, int amount, int time) {
        ArrayList<Triple<Double, Double, Double>> particleDirections = new ArrayList<>();

        particleDirections.add(Triple.of(0D, 0D, 0D));

        if (canSpreadTo(pLevel, pPos, gas, flowDirection))
            particleDirections.add(Triple.of(0D, 1/16D, 0D));
        else
            for (Direction dir : Direction.Plane.HORIZONTAL)
                if (canSpreadTo(pLevel, pPos, gas, dir))
                    particleDirections.add(Triple.of(dir.getStepX()/16D, 0D, dir.getStepZ()/16D));

        for (int i = 0; i < amount; i++)
            for (Triple<Double, Double, Double> direction : particleDirections) {
                Particle particle = Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.CLOUD,pPos.getX() + Math.random(), pPos.getY() + Math.random(), pPos.getZ() + Math.random(), direction.getLeft(), direction.getMiddle(), direction.getRight());
                assert particle != null;
                particle.setAlpha(0.2f);
                int color = gas.getAttributes().getColor(pLevel, pPos);

                particle.setColor(FastColor.ARGB32.red(color) / 256f, FastColor.ARGB32.green(color) / 256f, FastColor.ARGB32.blue(color) / 256f);
                particle.setLifetime(time);
            }
    }

    private static boolean canSpreadTo(BlockAndTintGetter pTint, BlockPos pPos, FlowingGas gas, Direction direction) {
        BlockState state = pTint.getBlockState(pPos.relative(direction));
        if (state.getFluidState().is(gas.getSource())) return true;
        if (state.getFluidState().is(gas.getFlowing())) return true;
        return state.canBeReplaced(gas);
    }

    @Override
    public @NotNull VoxelShape getShape(FluidState p_76084_, BlockGetter p_76085_, BlockPos p_76086_) {
        return Shapes.block();
    }

    @Api
    public static class Properties extends ForgeFlowingFluid.Properties {
        public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, FluidAttributes.Builder attributes) {
            super(still, flowing, attributes);
        }

        protected GasFlowDirection gasFlowDirection = GasFlowDirection.UP;

        protected double risePossibility = 1f;

        @Api
        public Properties gasFlowDirection(GasFlowDirection direction)
        {
            gasFlowDirection = direction;
            return this;
        }

        @Api
        public Properties risePossibility(double risePossibility) {
            this.risePossibility = risePossibility;
            return this;
        }

        public enum GasFlowDirection {
            UP(Direction.UP), DOWN(Direction.DOWN);

            private final Direction direction;

            GasFlowDirection(Direction direction) {
                if (direction.getStepY() == 0) throw new IllegalStateException("Gas flow direction must be vertical");

                this.direction = direction;
            }
        }

        // Casts to our own Properties

        public Properties canMultiply()
        {
            return (Properties) super.canMultiply();
        }

        public Properties bucket(Supplier<? extends Item> bucket)
        {
            return (Properties) super.bucket(bucket);
        }

        public Properties block(Supplier<? extends LiquidBlock> block)
        {
            return (Properties) super.block(block);
        }

        public Properties slopeFindDistance(int slopeFindDistance)
        {
            throw new UnsupportedOperationException("Gas fluids cannot have a slope find distance property");
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock)
        {
            throw new UnsupportedOperationException("Gas fluids cannot have a level decrease per block property");
        }

        public Properties explosionResistance(float explosionResistance)
        {
            return (Properties) super.explosionResistance(explosionResistance);
        }

        public Properties tickRate(int tickRate)
        {
            return (Properties) super.tickRate(tickRate);
        }
    }

    @Api
    public static class Flowing extends FlowingGas {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource() {
            return false;
        }
    }

    @Api
    public static class Source extends FlowingGas {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource() {
            return true;
        }
    }

    @Override
    public List<FluidBehavior> getBehaviors() {
        List<FluidBehavior> behaviors = new ArrayList<>();

        // Temporary STARTS
        behaviors.add(new HydrateBehavior(true));
        // Temporary ENDS

        behaviors.add(new FluidBehavior() {
            @Override
            public boolean tick(Level p_76113_, BlockPos p_76114_, FluidState p_76115_) {
                return true;
            }
        });
        return behaviors;
    }
}

