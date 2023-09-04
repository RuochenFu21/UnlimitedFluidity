package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * <p>This interface is where you actually implement the behavior of the fluid.</p>
 * @since       2.0
 */
@Api
public interface IInteractionWithNeighborBlockUpdateBehavior extends IInteractionBehavior, IInteractionWithNeighborBlockUpdate {
    @Override
    default void neighborChange(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos thisPos, @NotNull Block block, @NotNull BlockPos neighborPos, boolean idkRandomParameter) {
        interactWithNeighborBlock(level, thisPos, neighborPos);
    }

    @Override
    default boolean requireBlockBeBehaviorable() {
        return true;
    }
}
