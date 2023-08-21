package com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming;

/**
 * @since       1.1
 * For Custom fluid that allows/disallows player to swim in it
 * @param canSwim whether the player can swim in this fluid
 */
public record SwimmingFluidBehaviorImpl(boolean canSwim) implements ISwimmingFluidBehavior {
    /**
     * @since       1.1
     * For Custom fluid that allows player to swim in it
     */
    public SwimmingFluidBehaviorImpl() {
        this(true);
    }
}
