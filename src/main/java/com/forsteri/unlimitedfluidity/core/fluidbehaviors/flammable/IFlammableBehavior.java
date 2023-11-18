package com.forsteri.unlimitedfluidity.core.fluidbehaviors.flammable;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.ontouch.IInteractionWithNeighborBlockUpdateBehavior;
import com.forsteri.unlimitedfluidity.entry.FluidityTags;
import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Api for creating fluid behavior making fluid flammable</p>
 * <p>This is a interface for {@link FlammableBehavior} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface IFlammableBehavior extends IInteractionWithNeighborBlockUpdateBehavior {
    @Override
    default boolean tick(Level p_76113_, BlockPos p_76114_, FluidState p_76115_) {
        for (Direction direction : Direction.values()) {
            interactWithBlock(p_76113_, p_76114_.relative(direction), p_76113_.getBlockState(p_76114_.relative(direction)));
        }

        return false;
    }

    @Override
    default void interactWithBlock(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState interactant) {
        IInteractionWithNeighborBlockUpdateBehavior.super.interactWithBlock(level, pos, interactant);

        if (!interactant.is(FluidityTags.FluidityBlockTags.HAS_FIRE.getTag()))
            return;

        spawnParticles(level, pos);
        afterBurnt(level, pos);
    }

    /**
     * Spawn the flame particles
     * @param level the level
     * @param pos the position
     */
    @Api
    default void spawnParticles(LevelAccessor level, BlockPos pos) {
        for (int i = 0; i < 7; i++) {
            if (level instanceof ServerLevel serverLevel)
                serverLevel.sendParticles(ParticleTypes.FLAME, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), (int) (Math.random() * 100), 0, 0, 0, 0);
        }
    }

    /**
     * Behavior after the block is burnt
     * @param level the level
     * @param pos the position
     */
    default void afterBurnt(LevelAccessor level, BlockPos pos) {
        level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 11);
        level.getEntities((Entity) null, entityBurningArea(pos), entity -> entity instanceof LivingEntity).forEach(
                entity -> entity.setSecondsOnFire(5)
        );
    }

    /**
     * Area that the entity will be burnt
     * @param pos the center
     */
    default AABB entityBurningArea(BlockPos pos) {
        return new AABB(pos).expandTowards(1.0, 1.0, 1.0).expandTowards(-1.0, -1.0, -1.0);
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 300;
    }
}
