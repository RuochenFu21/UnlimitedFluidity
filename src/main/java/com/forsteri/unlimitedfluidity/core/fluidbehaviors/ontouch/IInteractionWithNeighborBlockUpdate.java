package com.forsteri.unlimitedfluidity.core.fluidbehaviors.ontouch;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableLiquidBlock;
import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * <p>{@link IInteraction} but with neighbor block update</p>
 * <p>I split this from {@link IInteraction} just because neighbor block update require the fluid to be {@link BehaviorableLiquidBlock}, so splitting them can give the developer ability to use less of {@link BehaviorableLiquidBlock}</p>
 * <p>Usually you will never need this, just use {@link IInteractionWithNeighborBlockUpdateBehavior}</p>
 * @since       2.0
 */
public interface IInteractionWithNeighborBlockUpdate extends IInteraction {
    /**
     * <p>Interact with a block *when* the fluid destroyed the block</p>
     * <p>This will default redirect to {@link IInteraction#interactWithBlock(LevelAccessor, BlockPos, BlockState)}</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactantPos The position of the block that is interacting with the block
     * @since       2.0
     */
    @Api
    default void interactWithNeighborBlock(LevelAccessor level, BlockPos pos, BlockPos interactantPos) {
        interactWithBlock(level, pos, level.getBlockState(interactantPos));
    }
}
