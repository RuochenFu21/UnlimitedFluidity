package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration;

/**
 * @since       1.1
 * For custom fluid that allows/disallows concrete hydration
 * @param canHydrateConcrete whether the fluid can are able to hydrate concrete
 */
public record ConcreteHydrationFluidBehaviorImpl(boolean canHydrateConcrete) implements IConcreteHydrationFluidBehavior {
    /**
     * @since       1.1
     * For custom fluid that don't concrete hydration
     */
    public ConcreteHydrationFluidBehaviorImpl() {
        this(false);
    }
}
