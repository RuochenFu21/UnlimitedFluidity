package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.sugarcanehydration;

/**
 * @since       1.1
 * For custom fluid that allows/disallows sugarcane hydration
 * @param canHydrateSugarCane whether the fluid can are able to hydrate farmland
 */
public record SugarCaneHydrationFluidBehaviorImpl(boolean canHydrateSugarCane) implements ISugarCaneHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't sugarcane hydration
     */
    public SugarCaneHydrationFluidBehaviorImpl() {
        this(false);
    }
}
