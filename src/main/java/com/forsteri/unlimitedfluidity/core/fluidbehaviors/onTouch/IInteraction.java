package com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.ParametersAreNonnullByDefault;

@Api
@ParametersAreNonnullByDefault
public interface IInteraction<T> {
    @Api
    default T interactWithEntity(BlockPos pos, Entity interactant) {
        if (interactant instanceof ItemEntity itemEntity)
            interactWithItem(interactant.getLevel(), pos, itemEntity.getItem());
        return null;
    }

    @Api
    default T interactWithBlock(LevelReader level, BlockPos pos, BlockState interactant) {
        if (interactant.getFluidState().is(Fluids.EMPTY))
            interactWithFluid(level, pos, interactant.getFluidState().getType());
        return null;
    }

    @Api
    default T interactWithBlockDestroyed(LevelReader level, BlockPos pos, BlockState interactant) {
        return interactWithBlock(level, pos, interactant);
    }

    @Api
    default T interactWithFluid(LevelReader level, BlockPos pos, Fluid interactant) {
        return null;
    }

    @Api
    default T interactWithItem(LevelReader level, BlockPos pos, ItemStack interactant) {
        return null;
    }
}
