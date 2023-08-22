package com.forsteri.unlimitedfluidity.core.fluidbehaviors.farmlandhydration;

/**
 * @since       1.1
 * For Custom fluid that allows/disallows farmland hydration
 * @param canHydrate whether the fluid can are able to hydrate farmland
 */
public record FarmLandHydrationFluidBehaviorImpl(boolean canHydrate) implements IFarmLandHydrationFluidBehavior {
    /**
     * @since       1.1
     * For Custom fluid that don't farmland hydration
     */
    public FarmLandHydrationFluidBehaviorImpl() {
        this(false);
    }
}
