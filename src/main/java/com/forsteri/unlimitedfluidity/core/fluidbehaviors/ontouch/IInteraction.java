package com.forsteri.unlimitedfluidity.core.fluidbehaviors.ontouch;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * <p>Api for creating behavior interface that have interactions between entity, block, fluid, or item</p>
 * <p>I split this from {@link IInteractionBehavior} just because to separate the abstract interactions and functionality implementation, usually you will neven need this class</p>
 * <p>Usually you will never need this, just use {@link IInteractionBehavior}</p>
 * @since       2.0
 */
@Api
@ParametersAreNonnullByDefault
public interface IInteraction {
    /**
     * <p>Interact with an entity</p>
     * <p>This will default call {@link IInteraction#interactWithItem(LevelAccessor, BlockPos, ItemStack)}</p> when it's a {@link ItemEntity}
     * @param pos The position of the block
     * @param interactant The entity that is interacting with the fluid
     
     * @since       2.0
     */
    @Api
    default void interactWithEntity(BlockPos pos, Entity interactant) {
        if (interactant instanceof ItemEntity itemEntity)
            interactWithItem(interactant.getLevel(), pos, itemEntity.getItem());
       
    }

    /**
     * <p>Interact with a block</p>
     * <p>This will default call {@link IInteraction#interactWithFluid(LevelAccessor, BlockPos, Fluid)} when the block has a fluid state</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The block that is interacting with the fluid
     
     * @since       2.0
     */
    @Api
    default void interactWithBlock(LevelAccessor level, BlockPos pos, BlockState interactant) {
        if (interactant.getFluidState().is(Fluids.EMPTY))
            interactWithFluid(level, pos, interactant.getFluidState().getType());
    }

    /**
     * <p>Interact with a block *when* the fluid destroyed the block</p>
     * <p>This will default redirect to {@link IInteraction#interactWithBlock(LevelAccessor, BlockPos, BlockState)}</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The block that is interacting with the fluid
     
     * @since       2.0
     */
    @Api
    default void interactWithBlockDestroyed(LevelAccessor level, BlockPos pos, BlockState interactant) {
        interactWithBlock(level, pos, interactant);
    }

    /**
     * <p>Interact with a fluid</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The fluid that is interacting with the fluid
     * @since       2.0
     */
    @Api
    default void interactWithFluid(LevelAccessor level, BlockPos pos, Fluid interactant) {
       
    }

    /**
     * <p>Interact with an item</p>
     * @param level The level
     * @param pos The position of the block
     * @param interactant The item that is interacting with the fluid
     
     * @since       2.0
     */
    @Api
    default void interactWithItem(LevelAccessor level, BlockPos pos, ItemStack interactant) {
       
    }
}
