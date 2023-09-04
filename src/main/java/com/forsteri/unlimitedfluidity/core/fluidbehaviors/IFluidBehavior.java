package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

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

    /**
     * <p>Called when the block's neighbor changed</p>
     * <p>require {@link IFluidBehavior#requireBlockBeBehaviorable()} to be true</p>
     * @since       2.0
     * **/
    @Api
    default void neighborChange(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos thisPos, @NotNull Block block, @NotNull BlockPos neighborPos, boolean idkRandomParameter) {}

    /**
     * <p>Getting the object's flammability, </p>
     * <p>require {@link IFluidBehavior#requireBlockBeBehaviorable()} to be true</p>
     * @return Chance that fire will spread and consume this block, 300 being a 100% chance, 0, being a 0% chance.
     * @since       2.0
     * **/
    @Api
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Api
    default boolean requireBlockBeBehaviorable() {
        return false;
    }
}
