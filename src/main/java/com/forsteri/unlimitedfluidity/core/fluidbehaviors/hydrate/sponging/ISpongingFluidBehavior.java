package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.AgriculturalHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing fluid sponging</p>
 * <p>This is a interface for {@link AgriculturalHydrationFluidBehavior} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface ISpongingFluidBehavior extends IFluidBehavior {
    /**
     * @return      whether the implementation allows fluid sponging
     * @since       2.0
     */
    @Api
    boolean canSponge();
}
