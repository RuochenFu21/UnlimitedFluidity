package com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface ISwimmingFluidBehavior extends FluidBehavior {
    @Api
    boolean canSwim();
}
