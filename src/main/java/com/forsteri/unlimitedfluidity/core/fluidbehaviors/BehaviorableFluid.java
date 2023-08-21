package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import com.forsteri.unlimitedfluidity.core.SmartFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BehaviorableFluid extends SmartFluid {
    protected BehaviorableFluid(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@NotNull Level p_76113_, @NotNull BlockPos p_76114_, @NotNull FluidState p_76115_) {
        if (getBehaviors().stream().anyMatch(behavior -> behavior.tick(p_76113_, p_76114_, p_76115_)))
            return;

        super.tick(p_76113_, p_76114_, p_76115_);
    }

    protected List<FluidBehavior> getBehaviors() {
        return new ArrayList<>();
    }

    public <T extends FluidBehavior> T getBehavior(Class<T> behavior) {
        //noinspection unchecked
        return (T) getBehaviors().stream().filter(behavior::isInstance).findFirst().orElse(null);
    }
}
