package org.forsteri123.unlimitedfluidity.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class RisingGas extends ForgeFlowingFluid {
    protected RisingGas(ForgeFlowingFluid.Properties properties) {
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
            if (pPos.getY() == (pLevel.getMaxBuildHeight() - 1)) {
                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                return;
            }
            if (canSpreadTo(pLevel, pPos.above())) {
                if (pPos.getY() != (pLevel.getMaxBuildHeight() - 1)) {
                    pLevel.setBlock(pPos.above(), this.createLegacyBlock(pState), 3);
                }
                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                int sides = 0;
                for (Direction dir : Direction.Plane.HORIZONTAL) if (canSpreadTo(pLevel, pPos.relative(dir))) sides++;

                if(pState.getAmount() - sides + 1 > 0) for (Direction dir : Direction.Plane.HORIZONTAL) if (canSpreadTo(pLevel, pPos.relative(dir)))
                    pLevel.setBlock(pPos.relative(dir), this.getFlowing(pState.getAmount() - sides + 1, false).createLegacyBlock(), 3);
                if(pState.getAmount() - sides + 1 > 0 && sides > 0) pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                assert true;
            }
        }
    }

    protected boolean canSpreadTo(LevelAccessor pLevel, BlockPos pPos){
        return (pLevel.getBlockState(pPos).is(Blocks.AIR) || pLevel.getBlockState(pPos).getBlock() instanceof IFluidBlock);
    }
}

