package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration;

import com.forsteri.unlimitedfluidity.util.Api;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.SugarCaneHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.FarmlandHydrationFluidBehaviorImpl;


/**
 * <p>Default fluid behavior implementation for allowing/disallowing agricultural hydration</p>
 * <p>This has the behavior of {@link FarmlandHydrationFluidBehaviorImpl} & {@link SugarCaneHydrationFluidBehaviorImpl}</p>
 * @since       2.0
 * @param canAgriculturalHydrate whether the fluid can are able to agricultural hydrate
 */
@Api
public record AgriculturalHydrationFluidBehaviorImpl(boolean canAgriculturalHydrate) implements IAgriculturalHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing agricultural hydration</p>
     * @since       2.0
     */
    @Api
    public AgriculturalHydrationFluidBehaviorImpl() {
        this(false);
    }
}
