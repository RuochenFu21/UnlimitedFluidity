package com.forsteri.unlimitedfluidity.core.flowinggas;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.farmlandhydration.FarmLandHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.sponging.SpongingFluidBehaviorImpl;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
public abstract class FlowingGas extends BehaviorableFluid {
    public static final int MAX_DENSITY = 128;
    public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_DENSITY);
    public static final Map<LevelAccessor, Map<BlockPos, Direction>> gasMovementMap = new HashMap<>();

    protected FlowingGas(ForgeFlowingFluid.Properties properties) {
        super(properties);

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
        return getMovementHandler(level).graph;
    }

    @Override
    protected void spread(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        if (pState.isEmpty()) return;

        if (spreadVertically(pLevel, pPos, pState, true))
            spreadVertically(pLevel, pPos, pState, false);
        else if (moveInPath(pLevel, pPos, pState, true))
            moveInPath(pLevel, pPos, pState, false);
        else if (spreadHorizontally(pLevel, pPos, true))
            spreadHorizontally(pLevel, pPos, false);
        else findAndMoveUp(pLevel, pPos, pState, false);

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
        invalid |= !getGasMap(pLevel).containsKey(pPos.relative(direction)) && direction != Direction.UP;

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
    protected static boolean looped(LevelAccessor pLevel, BlockPos pPos) {
        Direction gasMovement = getGasMap(pLevel).get(pPos);
        if (loopCheck.contains(pPos) || gasMovement == null || gasMovement == Direction.UP) {
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
        if (!canSpreadTo(pLevel, pPos.above())) return false;
        if (pState.getValue(FlowingGas.DENSITY) > MAX_DENSITY - getMovementHandler(pLevel).getDensity(pPos.above())) return false;

        if (simulate) return true;

        if (!pLevel.getBlockState(pPos.above()).isAir())
            beforeDestroyingBlock(pLevel, pPos.above(), pLevel.getBlockState(pPos.above()));

        getMovementHandler(pLevel).rise(pPos, Math.min(pState.getValue(FlowingGas.DENSITY), MAX_DENSITY - getMovementHandler(pLevel).getDensity(pPos.above())));
        getGraph(pLevel).addVertex(pPos);
        getGraph(pLevel).addVertex(pPos.above());
        getGraph(pLevel).addEdge(pPos, pPos.above());

        return true;
    }

    protected boolean spreadHorizontally(LevelAccessor pLevel, BlockPos pPos, boolean simulated) {
        Graph<BlockPos, DefaultEdge> graph = getGraph(pLevel);
        int totalDensity = getMovementHandler(pLevel).getDensity(pPos);
        int space = 1;
        Map<Direction, Integer> addedAmountMap = new HashMap<>();

        for (Direction direction : Direction.Plane.HORIZONTAL)
            if (canSpreadTo(pLevel, pPos.relative(direction))) {
                space++;
                totalDensity += getMovementHandler(pLevel).getDensity(pPos.relative(direction));
                addedAmountMap.put(direction, 0);

                for (BlockPos pos : new BlockPos[] {pPos, pPos.relative(direction)})
                    if (!graph.containsVertex(pos))
                        graph.addVertex(pos);

                if (!graph.containsEdge(pPos, pPos.relative(direction)))
                    graph.addEdge(pPos, pPos.relative(direction));
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

        addedAmountMap.forEach((direction, amount) ->
                addedAmountMap.put(direction, each - getMovementHandler(pLevel).getDensity(pPos.relative(direction)))
        );

        if (remainder != 0) {
            Set<Direction> remainderDirections = addedAmountMap.keySet();
            while (true) {
                Optional<Integer> optionalMin = addedAmountMap.values().stream().min(Integer::compareTo);

                if (optionalMin.isEmpty()) return false;

                Integer min = optionalMin.get();

                //noinspection OptionalGetWithoutIsPresent
                remainderDirections.remove(addedAmountMap.entrySet().stream().filter(entry -> entry.getValue().equals(min)).findFirst().get().getKey());

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

        for (Map.Entry<Direction, Integer> entry : addedAmountMap.entrySet()) {
            BlockPos pos = pPos.relative(entry.getKey());
            if (!pLevel.getBlockState(pos).isAir())
                beforeDestroyingBlock(pLevel, pos, pLevel.getBlockState(pos));

            getMovementHandler(pLevel).move(pPos, entry.getValue(), entry.getKey());
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

    public static void render(BlockAndTintGetter pLevel, BlockPos pPos, FlowingGas gas, int amount, int time) {
        ArrayList<Triple<Double, Double, Double>> particleDirections = new ArrayList<>();

        particleDirections.add(Triple.of(0D, 0D, 0D));

        if (canSpreadTo(pLevel, pPos, gas, Direction.UP))
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

    public static class Flowing extends FlowingGas {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource() {
            return false;
        }
    }

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
        behaviors.add(new SpongingFluidBehaviorImpl(true));
        behaviors.add(new FarmLandHydrationFluidBehaviorImpl(true));
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

