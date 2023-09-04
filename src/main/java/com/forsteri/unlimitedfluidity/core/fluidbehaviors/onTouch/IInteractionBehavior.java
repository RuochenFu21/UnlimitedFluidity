package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * <p>This interface is where you actually implement the behavior of the fluid.</p>
 * @since       2.0
 */
@Api
public interface IInteractionBehavior extends IFluidBehavior, IInteraction {
    @Override
    default boolean beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        interactWithBlockDestroyed(worldIn, pos, state);
        return false;
    }
}
