package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * <p>This interface is where you actually implement the behavior of the fluid.</p>
 * @param <T> The type of the return value of the interaction, just a bit of customization
 * @since       2.0
 */
@Api
public interface IInteractionWithNeighborBlockUpdateBehavior<T> extends IInteractionWithNeighborBlockUpdate<T>, IInteractionBehavior<T> {
    @Override
    default boolean onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        interactWithNeighborBlock(level, pos, neighbor);
        return false;
    }

    @Override
    default boolean requireBlockBeBehaviorable() {
        return true;
    }
}
