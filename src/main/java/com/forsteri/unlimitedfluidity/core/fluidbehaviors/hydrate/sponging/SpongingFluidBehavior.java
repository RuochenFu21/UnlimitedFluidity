package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing fluid sponging</p>
 * @since       2.0
 * @param canSponge whether the fluid can be sponged fluid
 */
@Api
public record SpongingFluidBehavior(boolean canSponge) implements ISpongingFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing sponging fluid</p>
     * @since       2.0
     */
    @Api
    public SpongingFluidBehavior() {
        this(false);
    }
}
