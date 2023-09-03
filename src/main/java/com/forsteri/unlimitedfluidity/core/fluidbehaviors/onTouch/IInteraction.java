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


/**
 * <p>Api for creating behavior interface that have interactions between entity, block, fluid, or item</p>
 * <p>I split this from {@link IInteractionBehavior} just because to separate the abstract interactions and functionality implementation, usually you will neven need this class</p>
 * <p>Usually you will never need this, just use {@link IInteractionBehavior}</p>
 * @param <T> The type of the return value of the interaction, just a bit of customization
 * @since       2.0
 */
@Api
@ParametersAreNonnullByDefault
public interface IInteraction<T> {
    /**
     * <p>Interact with an entity</p>
     * <p>This will default call {@link IInteraction#interactWithItem(LevelReader, BlockPos, ItemStack)}</p> when it's a {@link ItemEntity}
     * @param pos The position of the block
     * @param interactant The entity that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithEntity(BlockPos pos, Entity interactant) {
        if (interactant instanceof ItemEntity itemEntity)
            interactWithItem(interactant.getLevel(), pos, itemEntity.getItem());
        return null;
    }

    /**
     * <p>Interact with a block</p>
     * <p>This will default call {@link IInteraction#interactWithFluid(LevelReader, BlockPos, Fluid)} when the block has a fluid state</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The block that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithBlock(LevelReader level, BlockPos pos, BlockState interactant) {
        if (interactant.getFluidState().is(Fluids.EMPTY))
            interactWithFluid(level, pos, interactant.getFluidState().getType());
        return null;
    }

    /**
     * <p>Interact with a block *when* the fluid destroyed the block</p>
     * <p>This will default redirect to {@link IInteraction#interactWithBlock(LevelReader, BlockPos, BlockState)}</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The block that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithBlockDestroyed(LevelReader level, BlockPos pos, BlockState interactant) {
        return interactWithBlock(level, pos, interactant);
    }

    /**
     * <p>Interact with a fluid</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The fluid that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithFluid(LevelReader level, BlockPos pos, Fluid interactant) {
        return null;
    }

    /**
     * <p>Interact with an item</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The item that is interacting with the block
     * @return The return value, default null
     * @since       2.0
     */
    @Api
    default T interactWithItem(LevelReader level, BlockPos pos, ItemStack interactant) {
        return null;
    }
}
