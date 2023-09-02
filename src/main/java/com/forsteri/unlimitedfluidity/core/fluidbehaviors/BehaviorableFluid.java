package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import com.forsteri.unlimitedfluidity.core.SmartFluid;
import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Fluid that can add behaviors</p>
 * @since       2.0
 * **/
@Api
public abstract class BehaviorableFluid extends SmartFluid {
    protected BehaviorableFluid(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull FluidState state) {
        if (getBehaviors().stream().anyMatch(behavior -> behavior.tick(worldIn, pos, state)))
            return;

        super.tick(worldIn, pos, state);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (getBehaviors().stream().anyMatch(behavior -> behavior.beforeDestroyingBlock(worldIn, pos, state)))
            return;

        super.beforeDestroyingBlock(worldIn, pos, state);
    }

    protected void onEntityInside(LevelAccessor worldIn, Entity entity) {
        getBehaviors().forEach(behavior -> behavior.onEntityInside(worldIn, entity));
    }

    /**
     * @return      Returns the behaviors that this fluid has
     * @since       2.0
     * **/
    @Api
    protected List<FluidBehavior> getBehaviors() {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends FluidBehavior> T getBehavior(Class<T> behavior) {
        return (T) getBehaviors().stream().filter(behavior::isInstance).findFirst().orElse(null);
    }
}
