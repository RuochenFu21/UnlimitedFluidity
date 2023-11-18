package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.AgriculturalHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.FarmlandHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.SugarCaneHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.ConcreteHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.SpongingFluidBehavior;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing hydration</p>
 * <p>This has the behavior of {@link AgriculturalHydrationFluidBehavior}({@link FarmlandHydrationFluidBehavior} &amp; {@link SugarCaneHydrationFluidBehavior}) &amp; {@link ConcreteHydrationFluidBehavior} &amp; {@link SpongingFluidBehavior}</p>
 * @since       2.0
 * @param canHydrate whether the fluid can are able to hydrate
 */
@Api
public record HydrateBehavior(boolean canHydrate) implements IHydrateBehavior {
    /**
     * <p>Fluid behavior implementation disallowing hydration</p>
     * @since       2.0
     */
    @Api
    public HydrateBehavior() {
        this(false);
    }
}
