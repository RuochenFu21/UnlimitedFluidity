package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for allowing/disallowing concrete hydration</p>
 * @since       2.0
 * @param canHydrateConcrete whether the fluid can are able to hydrate concrete
 */
@Api
public record ConcreteHydrationFluidBehaviorImpl(boolean canHydrateConcrete) implements IConcreteHydrationFluidBehavior {
    /**
     * <p>Fluid behavior implementation disallowing concrete hydration</p>
     * @since       2.0
     */
    @Api
    public ConcreteHydrationFluidBehaviorImpl() {
        this(false);
    }
}
