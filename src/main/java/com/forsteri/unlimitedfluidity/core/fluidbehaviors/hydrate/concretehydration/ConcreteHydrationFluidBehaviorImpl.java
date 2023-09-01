package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * @since       1.1
 * For custom fluid that allows/disallows concrete hydration
 * @param canHydrateConcrete whether the fluid can are able to hydrate concrete
 */
@Api
public record ConcreteHydrationFluidBehaviorImpl(boolean canHydrateConcrete) implements IConcreteHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't concrete hydration
     */
    @Api
    public ConcreteHydrationFluidBehaviorImpl() {
        this(false);
    }
}
