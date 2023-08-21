package com.forsteri.unlimitedfluidity.core.fluidbehaviors.sponging;

/**
 * @since       1.1
 * For Custom fluid that allows/disallows fluid to be sponged
 * @param canSponge whether the fluid can be sponged
 */
public record SpongingFluidBehaviorImpl(boolean canSponge) implements ISpongingFluidBehavior {
    /**
     * @since       1.1
     * For Custom fluid that allows fluid to be sponged
     */
    public SpongingFluidBehaviorImpl() {
        this(true);
    }
}
