package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing farmland hydration</p>
 * @since       2.0
 * @param canHydrateFarmland whether the fluid can hydrate concrete
 */
@Api
public record FarmlandHydrationFluidBehavior(boolean canHydrateFarmland) implements IFarmlandHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing farmland hydration</p>
     * @since       2.0
     */
    @Api
    public FarmlandHydrationFluidBehavior() {
        this(false);
    }
}
