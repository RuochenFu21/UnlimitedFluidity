package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.farmlandhydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * @since       1.1
 * For custom fluid that allows/disallows farmland hydration
 * @param canHydrateFarmland whether the fluid can are able to hydrate farmland
 */
@Api
public record FarmlandHydrationFluidBehaviorImpl(boolean canHydrateFarmland) implements IFarmlandHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't farmland hydration
     */
    @Api
    public FarmlandHydrationFluidBehaviorImpl() {
        this(false);
    }
}
