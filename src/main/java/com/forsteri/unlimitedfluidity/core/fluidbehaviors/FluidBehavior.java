package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

@Api
public interface FluidBehavior {
    /**
     * @since       1.1
     * @return      true if stop the original tick method
     * **/
    @Api
    default boolean tick(Level p_76113_, BlockPos p_76114_, FluidState p_76115_) {
        return false;
    }

    @Api
    default boolean beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        return false;
    }
}
