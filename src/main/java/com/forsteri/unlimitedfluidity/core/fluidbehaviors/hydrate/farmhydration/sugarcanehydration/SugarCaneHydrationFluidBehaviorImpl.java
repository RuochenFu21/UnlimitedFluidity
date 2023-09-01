package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.sugarcanehydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * @since       1.1
 * For custom fluid that allows/disallows sugarcane hydration
 * @param canHydrateSugarCane whether the fluid can are able to hydrate farmland
 */
@Api
public record SugarCaneHydrationFluidBehaviorImpl(boolean canHydrateSugarCane) implements ISugarCaneHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't sugarcane hydration
     */
    @Api
    public SugarCaneHydrationFluidBehaviorImpl() {
        this(false);
    }
}
