package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing sugarcane hydration</p>
 * <p>This is a interface for {@link SugarCaneHydrationFluidBehaviorImpl} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface ISugarCaneHydrationFluidBehavior extends IFluidBehavior {
    /**
     * @return      whether the implementation allows sugarcane hydration
     * @since       2.0
     */
    @Api
    boolean canHydrateSugarCane();
}
