package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing farmland hydration</p>
 * <p>This is a interface for {@link FarmlandHydrationFluidBehaviorImpl} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface IFarmlandHydrationFluidBehavior extends IFluidBehavior {
    /**
     * @return      whether the implementation allows concrete hydration
     * @since       2.0
     */
    @Api
    boolean canHydrateFarmland();
}
