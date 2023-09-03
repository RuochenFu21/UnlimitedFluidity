package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface IInteractionWIthNeighborBlock<T> extends IInteraction<T> {
    @Api
    default T interactWithNeighborBlock(LevelReader level, BlockPos pos, BlockPos interactantPos) {
        return interactWithBlock(level, pos, level.getBlockState(interactantPos));
    }
}
