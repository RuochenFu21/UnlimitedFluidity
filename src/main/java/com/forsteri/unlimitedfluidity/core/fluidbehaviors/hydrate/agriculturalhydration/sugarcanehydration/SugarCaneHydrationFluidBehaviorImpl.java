package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing sugarcane hydration</p>
 * @since       2.0
 * @param canHydrateSugarCane whether the fluid can are able to hydrate concrete
 */
@Api
public record SugarCaneHydrationFluidBehaviorImpl(boolean canHydrateSugarCane) implements ISugarCaneHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing sugarcane hydration</p>
     * @since       2.0
     */
    @Api
    public SugarCaneHydrationFluidBehaviorImpl() {
        this(false);
    }
}
