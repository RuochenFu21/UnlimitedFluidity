package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

/**
 * @since       1.1
 * For custom fluid that allows/disallows fluid to be sponged
 * @param canSponge whether the fluid can be sponged
 */
public record SpongingFluidBehaviorImpl(boolean canSponge) implements ISpongingFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't allow fluid to be sponged
     */
    public SpongingFluidBehaviorImpl() {
        this(false);
    }
}
