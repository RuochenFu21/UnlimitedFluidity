package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing concrete hydration</p>
 * <p>This is a interface for {@link ConcreteHydrationFluidBehavior} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface IConcreteHydrationFluidBehavior extends IFluidBehavior {
    /**
     * @return      whether the implementation allows concrete hydration
     * @since       2.0
     */
    @Api
    boolean canHydrateConcrete();
}
