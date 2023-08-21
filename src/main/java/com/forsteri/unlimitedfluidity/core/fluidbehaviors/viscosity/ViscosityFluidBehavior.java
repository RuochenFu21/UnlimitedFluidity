package com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity;

/**
 * @since       1.1
 * For Custom fluid that have entity movement viscosity.
 * @param viscosity how much slower the entity move in this fluid compare to water.
 */
public record ViscosityFluidBehavior(int viscosity) implements IViscosityFluidBehavior {}
