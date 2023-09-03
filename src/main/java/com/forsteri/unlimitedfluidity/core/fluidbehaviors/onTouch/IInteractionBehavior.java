package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface IInteractionBehavior<T> extends IInteraction<T>, IFluidBehavior {
    @Override
    default boolean beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        interactWithBlockDestroyed(worldIn, pos, state);
        return false;
    }
}
