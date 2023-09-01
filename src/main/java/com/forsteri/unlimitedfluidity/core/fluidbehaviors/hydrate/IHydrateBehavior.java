package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.IConcreteHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.IFarmHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.ISpongingFluidBehavior;

public interface IHydrateBehavior extends IConcreteHydrationFluidBehavior, IFarmHydrationFluidBehavior, ISpongingFluidBehavior {
    @Override
    default boolean canHydrateFarm() {
        return canHydrate();
    }

    @Override
    default boolean canSponge() {
        return canHydrate();
    }

    @Override
    default boolean canHydrateConcrete() {
        return canHydrate();
    }

    boolean canHydrate();
}
