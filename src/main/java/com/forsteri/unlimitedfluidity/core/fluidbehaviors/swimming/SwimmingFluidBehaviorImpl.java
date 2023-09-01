package com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * @since       1.1
 * For Custom fluid that allows/disallows player to swim in it
 * @param canSwim whether the player can swim in this fluid
 */
@Api
public record SwimmingFluidBehaviorImpl(boolean canSwim) implements ISwimmingFluidBehavior {
    /**
     * @since       1.1
     * For Custom fluid that allows player to swim in it
     */
    @Api
    public SwimmingFluidBehaviorImpl() {
        this(true);
    }
}
