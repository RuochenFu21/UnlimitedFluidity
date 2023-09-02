package com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior for entity movement resistance</p>
 * <p>This is a interface for {@link ViscosityFluidBehavior} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface IViscosityFluidBehavior extends FluidBehavior {
    /**
     * @return      how much slower the entity move in this fluid compare to water
     * @since       2.0
     */
    @Api
    int viscosity();
}
