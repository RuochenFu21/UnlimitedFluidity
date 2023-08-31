package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.farmlandhydration;

/**
 * @since       1.1
 * For custom fluid that allows/disallows farmland hydration
 * @param canHydrateFarmland whether the fluid can are able to hydrate farmland
 */
public record FarmlandHydrationFluidBehaviorImpl(boolean canHydrateFarmland) implements IFarmlandHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't farmland hydration
     */
    public FarmlandHydrationFluidBehaviorImpl() {
        this(false);
    }
}
