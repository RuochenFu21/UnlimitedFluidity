package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing farmland hydration</p>
 * @since       2.0
 * @param canHydrateFarmland whether the fluid can are able to hydrate concrete
 */
@Api
public record FarmlandHydrationFluidBehaviorImpl(boolean canHydrateFarmland) implements IFarmlandHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing farmland hydration</p>
     * @since       2.0
     */
    @Api
    public FarmlandHydrationFluidBehaviorImpl() {
        this(false);
    }
}
