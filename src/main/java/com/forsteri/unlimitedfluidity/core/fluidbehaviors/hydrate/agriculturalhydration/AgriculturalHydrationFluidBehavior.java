package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration;

import com.forsteri.unlimitedfluidity.util.Api;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.SugarCaneHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.FarmlandHydrationFluidBehavior;


/**
 * <p>Default fluid behavior implementation for allowing/disallowing agricultural hydration</p>
 * <p>This has the behavior of {@link FarmlandHydrationFluidBehavior} &amp; {@link SugarCaneHydrationFluidBehavior}</p>
 * @since       2.0
 * @param canAgriculturalHydrate whether the fluid can do agricultural hydrate
 */
@Api
public record AgriculturalHydrationFluidBehavior(boolean canAgriculturalHydrate) implements IAgriculturalHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing agricultural hydration</p>
     * @since       2.0
     */
    @Api
    public AgriculturalHydrationFluidBehavior() {
        this(false);
    }
}
