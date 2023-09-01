package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface IConcreteHydrationFluidBehavior extends FluidBehavior {
    @Api
    boolean canHydrateConcrete();
}
