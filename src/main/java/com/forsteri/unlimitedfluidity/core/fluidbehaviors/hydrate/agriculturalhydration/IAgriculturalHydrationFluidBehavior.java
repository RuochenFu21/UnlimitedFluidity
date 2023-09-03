package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.IFarmlandHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.ISugarCaneHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing agricultural hydration</p>
 * <p>This is a interface for {@link AgriculturalHydrationFluidBehaviorImpl} for allowance of further extending</p>
 * <p>This has the behavior of {@link IFarmlandHydrationFluidBehavior} &amp {@link ISugarCaneHydrationFluidBehavior}</p>
 * @since       2.0
 */
@Api
public interface IAgriculturalHydrationFluidBehavior extends ISugarCaneHydrationFluidBehavior, IFarmlandHydrationFluidBehavior {
    @Override
    default boolean canHydrateSugarCane() {
        return canAgriculturalHydrate();
    }

    @Override
    default boolean canHydrateFarmland() {
        return canAgriculturalHydrate();
    }

    /**
     * @return      whether the implementation allows agricultural hydration
     * @since       2.0
     */
    @Api
    boolean canAgriculturalHydrate();
}
