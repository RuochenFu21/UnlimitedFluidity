package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.sugarcanehydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface ISugarCaneHydrationFluidBehavior extends FluidBehavior {
    @Api
    boolean canHydrateSugarCane();
}
