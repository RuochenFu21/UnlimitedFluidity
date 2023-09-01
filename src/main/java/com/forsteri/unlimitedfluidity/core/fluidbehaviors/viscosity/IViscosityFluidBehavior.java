package com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public interface IViscosityFluidBehavior extends FluidBehavior {
    @Api
    int viscosity();
}
