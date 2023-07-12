package org.forsteri.unlimitedfluidity.core.flowingGas;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public abstract class FlowingGas extends ForgeFlowingFluid {
    public static final int MAX_AMOUNT = 2520;
    public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_AMOUNT);

    protected FlowingGas(ForgeFlowingFluid.Properties properties) {
        super(properties);
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

    @Override
    protected void spread(LevelAccessor pLevel, BlockPos pPos, FluidState pState) {
        if (!pState.isEmpty()) {
            if (canSpreadTo(pLevel, pPos.above())) {
                if (pPos.getY() != (pLevel.getMaxBuildHeight() - 1)) {
                    pLevel.setBlock(pPos.above(), this.createLegacyBlock(pState), 3);
                }
                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                if (!isSource(pState)) return;

                boolean[][] blockedMatrix = new boolean[3][3];

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (!canSpreadTo(pLevel, pPos.relative(direction))) {
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
                        if (!canSpreadTo(pLevel, pPos.relative(Direction.EAST, 2 * i - 1).relative(Direction.SOUTH, 2 * j - 1))) {
                            blockedMatrix[2 * i][2 * j] = true;
                        }
                    }
                }

                int openSpaceCount = 3 * 3 - Arrays.deepToString(blockedMatrix).replaceAll("[^t]", "").length();

                if (openSpaceCount == 0) return;

                if (getAmount(pState) / openSpaceCount < 1) return;

                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (!blockedMatrix[i + 1][j + 1]) {
                            BlockPos newPos = pPos.offset(i, 0, j);
                            pLevel.setBlock(newPos, this.getFlowing(getAmount(pState) / openSpaceCount, false).createLegacyBlock(), 11);
                        }
                    }
                }
            }
        }
    }

//    @Override
//    protected BlockState createLegacyBlock(FluidState state)
//    {
//        if (super.createLegacyBlock(state).getBlock() instanceof AirBlock) return super.createLegacyBlock(state);
//
//        return super.createLegacyBlock(state).setValue(DENSITY, state.getValue(DENSITY));
//    }

    protected boolean canSpreadTo(LevelAccessor pLevel, BlockPos pPos){
        return (pLevel.getBlockState(pPos).is(Blocks.AIR)) || (pLevel.getBlockState(pPos).getMaterial().isReplaceable());
    }

    public @NotNull FluidState getFlowing(int p_75954_, boolean p_75955_) {
        return this.getFlowing().defaultFluidState().setValue(DENSITY, p_75954_).setValue(FALLING, p_75955_);
    }

    public static class Flowing extends FlowingGas {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(DENSITY, MAX_AMOUNT - 1));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(DENSITY);
        }

        public int getAmount(FluidState state) {
            return state.getValue(DENSITY);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends FlowingGas {
        public Source(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(DENSITY, MAX_AMOUNT));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(DENSITY);
        }

        @Override
        public int getAmount(FluidState p_164509_) {
            return MAX_AMOUNT;
        }

        @Override
        public boolean isSource(FluidState p_76140_) {
            return true;
        }
    }
}

