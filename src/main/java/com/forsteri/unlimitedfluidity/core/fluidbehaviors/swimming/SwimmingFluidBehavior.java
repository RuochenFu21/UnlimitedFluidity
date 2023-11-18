package com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing player to swim in the fluid</p>
 * @since       2.0
 * @param canSwim whether the player can swim in this fluid
 */
@Api
public record SwimmingFluidBehavior(boolean canSwim) implements ISwimmingFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing player to swim in the fluid</p>
     * @since       2.0
     */
    @Api
    public SwimmingFluidBehavior() {
        this(true);
    }
}
