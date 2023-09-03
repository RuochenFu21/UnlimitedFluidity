package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableLiquidBlock;
import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * <p>{@link IInteraction} but with neighbor block update</p>
 * <p>I split this from {@link IInteraction} just because neighbor block update require the fluid to be {@link BehaviorableLiquidBlock}, so splitting them can give the developer ability to use less of {@link BehaviorableLiquidBlock}</p>
 * <p>Usually you will never need this, just use {@link IInteractionWithNeighborBlockUpdateBehavior}</p>
 * @param <T> The type of the return value of the interaction, just a bit of customization
 * @since       2.0
 */
public interface IInteractionWithNeighborBlockUpdate<T> extends IInteraction<T> {
    /**
     * <p>Interact with a block *when* the fluid destroyed the block</p>
     * <p>This will default redirect to {@link IInteraction#interactWithBlock(LevelReader, BlockPos, BlockState)}</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactantPos The position of the block that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithNeighborBlock(LevelReader level, BlockPos pos, BlockPos interactantPos) {
        return interactWithBlock(level, pos, level.getBlockState(interactantPos));
    }
}
