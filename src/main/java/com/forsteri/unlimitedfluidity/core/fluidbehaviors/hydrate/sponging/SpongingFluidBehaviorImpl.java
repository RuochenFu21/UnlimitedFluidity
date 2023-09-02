package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing fluid sponging</p>
 * @since       2.0
 * @param canSponge whether the fluid can are able to sponge fluid
 */
@Api
public record SpongingFluidBehaviorImpl(boolean canSponge) implements ISpongingFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing sponging fluid</p>
     * @since       2.0
     */
    @Api
    public SpongingFluidBehaviorImpl() {
        this(false);
    }
}
