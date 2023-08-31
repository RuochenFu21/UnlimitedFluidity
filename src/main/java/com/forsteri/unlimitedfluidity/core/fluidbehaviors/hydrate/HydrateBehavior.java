package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.IConcreteHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.IFarmHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.ISpongingFluidBehavior;

public record HydrateBehavior(boolean canHydrate) implements IConcreteHydrationFluidBehavior, IFarmHydrationFluidBehavior, ISpongingFluidBehavior {
    @Override
    public boolean canHydrateFarm() {
        return canHydrate;
    }

    @Override
    public boolean canSponge() {
        return canHydrate;
    }

    @Override
    public boolean canHydrateConcrete() {
        return canHydrate;
    }
}
