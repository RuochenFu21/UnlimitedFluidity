package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * <p>Fluid behavior interface</p>
 * **/
@Api
public interface IFluidBehavior {
    /**
     * <p>Called when the fluid ticks</p>
     * @since       2.0
     * @return      true if stop {@link net.minecraft.world.level.material.FlowingFluid#tick(Level, BlockPos, FluidState)}
     * **/
    @Api
    default boolean tick(Level p_76113_, BlockPos p_76114_, FluidState p_76115_) {
        return false;
    }

    /**
     * <p>Called before the fluid destroying block</p>
     * @since       2.0
     * @return      true if stop {@link net.minecraftforge.fluids.ForgeFlowingFluid#beforeDestroyingBlock(LevelAccessor, BlockPos, BlockState)}
     * **/
    @SuppressWarnings("JavadocReference")
    @Api
    default boolean beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        return false;
    }

    //TODO: Add effect to this
    /**
     * <p>Called when there's an entity ticking inside the fluid</p>
     * @since       2.0
     * **/
    @Api
    default void onEntityInside(LevelAccessor worldIn, Entity entity) {}

    default boolean onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        return false;
    }

    default boolean requireBlockBeBehaviorable() {
        return false;
    }
}
