package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.farmlandhydration.IFarmlandHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.sugarcanehydration.ISugarCaneHydrationFluidBehavior;

public interface IFarmHydrationFluidBehavior extends ISugarCaneHydrationFluidBehavior, IFarmlandHydrationFluidBehavior {
    @Override
    default boolean canHydrateSugarCane() {
        return canHydrateFarm();
    }

    @Override
    default boolean canHydrateFarmland() {
        return canHydrateFarm();
    }

    boolean canHydrateFarm();
}
