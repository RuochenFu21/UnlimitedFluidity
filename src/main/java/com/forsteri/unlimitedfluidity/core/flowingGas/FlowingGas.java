package com.forsteri.unlimitedfluidity.core.flowingGas;

import com.forsteri.unlimitedfluidity.core.Spongability;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
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
public abstract class FlowingGas extends ForgeFlowingFluid implements Spongability {
    public boolean spongable(){
        return true;
    }
    public static final int MAX_DENSITY = 128;
    public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_DENSITY);
    public static final Map<LevelAccessor, Map<BlockPos, Direction>> gasMovementMap = new HashMap<>();

    @Override
    public FlowingGas getSource() {
        if (!(super.getSource() instanceof FlowingGas flowingGas))
            throw new IllegalStateException("Source of this gas is not a gas!");

        return flowingGas;
    }

    @Override
    public FlowingGas getFlowing() {
        if (!(super.getFlowing() instanceof FlowingGas flowingGas))
            throw new IllegalStateException("Flowing state of this gas is not a gas!");

        return flowingGas;
    }

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
    public void tick(Level pLevel, BlockPos pPos, FluidState pState) {
        if (pLevel.isClientSide) return;

        this.spread(pLevel, pPos, pState);
    }

    protected void spreadTo(LevelAccessor pLevel, BlockPos pPos, BlockState pBlockState, Direction pDirection, FluidState pFluidState) {
        if (!pBlockState.isAir()) {
            this.beforeDestroyingBlock(pLevel, pPos, pBlockState);
        }
        pLevel.setBlock(pPos, pFluidState.createLegacyBlock(), 3);

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

        for (Map.Entry<Direction, Integer> entry : addedAmountMap.entrySet())
            getMovementHandler(pLevel).move(pPos, entry.getValue(), entry.getKey());

        return true;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        if (super.createLegacyBlock(state).getBlock() instanceof AirBlock) return super.createLegacyBlock(state);

        return super.createLegacyBlock(state).setValue(DENSITY, state.getValue(DENSITY));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean canSpreadTo(LevelAccessor pLevel, BlockPos pPos){
        return getMovementHandler(pLevel).getDensity(pPos) != -1;
    }


    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        if (!this.isSource(this.defaultFluidState()))
            builder.add(LEVEL);
        builder.add(DENSITY);
    }

    public @NotNull FluidState getFlowing(int p_75954_, boolean p_75955_) {
        return this.getFlowing().defaultFluidState().setValue(DENSITY, p_75954_).setValue(FALLING, p_75955_);
    }

    @Override
    public int getAmount(FluidState pState) {
        return pState.hasProperty(DENSITY) ? pState.getValue(DENSITY) : 0;
    }

    abstract boolean isSource();

    @Override
    public boolean isSource(FluidState p_76140_) {
        return isSource();
    }

    public static class Flowing extends FlowingGas {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        boolean isSource() {
            return false;
        }
    }

    public static class Source extends FlowingGas {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        boolean isSource() {
            return true;
        }
    }
}

