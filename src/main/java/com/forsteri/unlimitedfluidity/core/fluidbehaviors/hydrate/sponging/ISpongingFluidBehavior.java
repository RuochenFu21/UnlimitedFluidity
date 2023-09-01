package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface ISpongingFluidBehavior extends FluidBehavior {
    @Api
    boolean canSponge();
}
