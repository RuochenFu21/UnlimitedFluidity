package com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * @since       1.1
 * For Custom fluid that have entity movement viscosity.
 * @param viscosity how much slower the entity move in this fluid compare to water.
 */
@Api
public record ViscosityFluidBehavior(int viscosity) implements IViscosityFluidBehavior {}
