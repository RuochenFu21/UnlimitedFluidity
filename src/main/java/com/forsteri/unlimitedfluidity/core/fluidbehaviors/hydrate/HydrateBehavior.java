package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.AgriculturalHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.FarmlandHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.SugarCaneHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.ConcreteHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.SpongingFluidBehaviorImpl;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing hydration</p>
 * <p>This has the behavior of {@link AgriculturalHydrationFluidBehaviorImpl}({@link FarmlandHydrationFluidBehaviorImpl} & {@link SugarCaneHydrationFluidBehaviorImpl}) & {@link ConcreteHydrationFluidBehaviorImpl} & {@link SpongingFluidBehaviorImpl}</p>
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
