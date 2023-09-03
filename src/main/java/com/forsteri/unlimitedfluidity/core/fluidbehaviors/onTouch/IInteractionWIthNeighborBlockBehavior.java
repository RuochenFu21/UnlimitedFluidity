package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

@Api
public interface IInteractionWIthNeighborBlockBehavior<T> extends IInteractionWIthNeighborBlock<T>, IInteractionBehavior<T> {
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
