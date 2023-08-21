package com.forsteri.unlimitedfluidity.core.fluidbehaviors.concretehydration;

/**
 * @since       1.1
 * For Custom fluid that allows/disallows concrete hydration
 * @param canHydrate whether the fluid can are able to hydrate concrete
 */
public record ConcreteHydrationFluidBehaviorImpl(boolean canHydrate) implements IConcreteHydrationFluidBehavior {
    /**
     * @since       1.1
     * For Custom fluid that allows concrete hydration
     */
    public ConcreteHydrationFluidBehaviorImpl() {
        this(true);
    }
}
