package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.FluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.AgriculturalHydrationFluidBehaviorImpl;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing fluid sponging</p>
 * <p>This is a interface for {@link AgriculturalHydrationFluidBehaviorImpl} for allowance of further extending</p>
 * @since       2.0
 */
@Api
public interface ISpongingFluidBehavior extends FluidBehavior {
    /**
     * @return      whether the implementation allows fluid sponging
     * @since       2.0
     */
    @Api
    boolean canSponge();
}
