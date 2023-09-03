package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.IAgriculturalHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.farmlandhydration.IFarmlandHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.ISugarCaneHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.IConcreteHydrationFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.ISpongingFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;

/**
 * <p>Api for creating fluid behavior allowing/disallowing hydration</p>
 * <p>This is a interface for {@link HydrateBehavior} for allowance of further extending</p>
 * <p>This has the behavior of {@link IAgriculturalHydrationFluidBehavior}({@link IFarmlandHydrationFluidBehavior} &amp {@link ISugarCaneHydrationFluidBehavior}) &amp {@link IAgriculturalHydrationFluidBehavior} &amp {@link ISpongingFluidBehavior}</p>
 * @since       2.0
 */
@Api
public interface IHydrateBehavior extends IConcreteHydrationFluidBehavior, IAgriculturalHydrationFluidBehavior, ISpongingFluidBehavior {
    @Override
    default boolean canAgriculturalHydrate() {
        return canHydrate();
    }

    @Override
    default boolean canSponge() {
        return canHydrate();
    }

    @Override
    default boolean canHydrateConcrete() {
        return canHydrate();
    }

    /**
     * @return      whether the implementation allows hydration
     * @since       2.0
     */
    @Api
    boolean canHydrate();
}
