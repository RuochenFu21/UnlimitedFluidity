package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.farmlandhydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface IFarmlandHydrationFluidBehavior extends FluidBehavior {
    @Api
    boolean canHydrateFarmland();
}
