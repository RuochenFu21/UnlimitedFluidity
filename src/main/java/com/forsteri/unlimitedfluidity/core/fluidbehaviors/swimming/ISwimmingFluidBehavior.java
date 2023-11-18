package com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing player to swim in the fluid</p>
 * <p>This is a interface for {@link SwimmingFluidBehavior} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface ISwimmingFluidBehavior extends IFluidBehavior {
    /**
     * @return      whether the implementation allows player to swim in the fluid
     * @since       2.0
     */
    @Api
    boolean canSwim();
}
