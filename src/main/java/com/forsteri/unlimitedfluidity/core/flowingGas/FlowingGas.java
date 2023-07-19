package com.forsteri.unlimitedfluidity.core.flowingGas;

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
import com.forsteri.unlimitedfluidity.core.Spongability;
import com.forsteri.unlimitedfluidity.util.Fragments;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.BreadthFirstIterator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;

@ParametersAreNonnullByDefault
public abstract class FlowingGas extends ForgeFlowingFluid implements Spongability {
    public boolean spongable(){
        return true;
    }
    public static final int MAX_AMOUNT = 128;
    public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_AMOUNT);

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
                    .setValue(DENSITY, MAX_AMOUNT)
            );
        } else {
            this.registerDefaultState(this.getStateDefinition().any()
                    .setValue(DENSITY, MAX_AMOUNT - 1)
                    .setValue(LEVEL, 7)
            );
        }
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, FluidState pState) {
        this.spread(pLevel, pPos, pState);
    }

    protected void spreadTo(LevelAccessor pLevel, BlockPos pPos, BlockState pBlockState, Direction pDirection, FluidState pFluidState) {
        if (!pBlockState.isAir()) {
            this.beforeDestroyingBlock(pLevel, pPos, pBlockState);
        }
        pLevel.setBlock(pPos, pFluidState.createLegacyBlock(), 3);

    }

    public GasMovementHandler movementHandler(LevelAccessor level) {
        return GasMovementHandler.getOrCreate(level, this);
    }

    @Override
    protected void spread(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        if (pState.isEmpty())  return;

        if (canForcedSpreadTo(pLevel, pState, pPos.above()))
            spreadVertically(pLevel, pPos, pState);
        else if (spreadHorizontally(pLevel, pPos, pState, true))
            spreadHorizontally(pLevel, pPos, pState, false);
        else findAndMoveUp(pLevel, pPos, pState);

        movementHandler(pLevel).tick();
    }

    protected void findAndMoveUp(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        if (!movementHandler(pLevel).graph.containsVertex(pPos)) return;

        Iterator<BlockPos> iterator = new BreadthFirstIterator<>(movementHandler(pLevel).graph, pPos);
        BlockPos destination = null;
        while (iterator.hasNext()) {
            destination = iterator.next();
            if (destination.getY() == pPos.getY() + 1) break;
        }

        if (destination == null) return;

        BFSShortestPath<BlockPos, DefaultEdge> pathFinder = new BFSShortestPath<>(movementHandler(pLevel).graph);

        GraphPath<BlockPos, DefaultEdge> path = pathFinder.getPath(pPos, destination);

        if (path.getVertexList().size() <= 1) return;

        System.out.println(path.getVertexList().get(1).subtract(pPos));

//        movementHandler(pLevel).graph
    }

    protected void spreadVertically(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        movementHandler(pLevel).rise(pPos, pState.getValue(FlowingGas.DENSITY));
        movementHandler(pLevel).graph.addVertex(pPos);
        movementHandler(pLevel).graph.addVertex(pPos.above());
        movementHandler(pLevel).graph.addEdge(pPos, pPos.above());
    }



    protected boolean spreadHorizontally(LevelAccessor pLevel, BlockPos pPos, FluidState pState, boolean simulate) {
        boolean[][] blockedMatrix = new boolean[3][3];

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!canSpreadTo(pLevel, pState, pPos.relative(direction))) {
                for (int i = 0; i < 3; i++) {
                    if (direction.getStepX() == 0) {
                        blockedMatrix[i][direction.getStepZ() + 1] = true;
                    } else {
                        blockedMatrix[direction.getStepX() + 1][i] = true;
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (!canSpreadTo(pLevel, pState, pPos.relative(Direction.EAST, 2 * i - 1).relative(Direction.SOUTH, 2 * j - 1))) {
                    blockedMatrix[2 * i][2 * j] = true;
                }
            }
        }

        int openSpaceCount;

        Loop: do {
            openSpaceCount = Fragments.countHierarchically(blockedMatrix, (Object object) -> object instanceof Boolean && !(boolean) object);

            if (getAmount(pState) / openSpaceCount < 1) return false;

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) continue;
                    if (!blockedMatrix[i + 1][j + 1]) {
                        BlockPos newPos = pPos.offset(i, 0, j);
                        if (movementHandler(pLevel).getDensity(newPos) > getAmount(pState) / openSpaceCount) {
                            blockedMatrix[i + 1][j + 1] = true;
                            continue Loop;
                        }
                    }
                }
            }

            movementHandler(pLevel).graph.addVertex(pPos);

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!blockedMatrix[i + 1][j + 1]) {
                        BlockPos newPos = pPos.offset(i, 0, j);
                        if (!newPos.equals(pPos)) {
                            movementHandler(pLevel).graph.addVertex(newPos);
                            movementHandler(pLevel).graph.addEdge(pPos, newPos);
                        }
                    }
                }
            }

            if (simulate) return true;

            movementHandler(pLevel).increase(pPos, - getAmount(pState));

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!blockedMatrix[i + 1][j + 1]) {
                        BlockPos newPos = pPos.offset(i, 0, j);
                        movementHandler(pLevel).increase(newPos, getAmount(pState) / openSpaceCount);
                    }
                }
            }

            return true;
        } while (openSpaceCount > 0);

        return false;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        if (super.createLegacyBlock(state).getBlock() instanceof AirBlock) return super.createLegacyBlock(state);

        return super.createLegacyBlock(state).setValue(DENSITY, state.getValue(DENSITY));
    }

    protected boolean canSpreadTo(LevelAccessor pLevel, FluidState state, BlockPos pPos){
        return canForcedSpreadTo(pLevel, state, pPos)
                && movementHandler(pLevel).getDensity(pPos) < getAmount(state);
    }

    protected boolean canForcedSpreadTo(LevelAccessor pLevel, FluidState state, BlockPos pPos){
        return movementHandler(pLevel).getDensity(pPos) != -1
                && movementHandler(pLevel).getDensity(pPos) + getAmount(state) <= MAX_AMOUNT;
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
