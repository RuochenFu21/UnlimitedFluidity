package com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity;

import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Default fluid behavior implementation for entity movement resistance</p>
 * @since       2.0
 * @param viscosity whether the player can swim in this fluid
 */
@Api
public record ViscosityFluidBehavior(int viscosity) implements IViscosityFluidBehavior {}
