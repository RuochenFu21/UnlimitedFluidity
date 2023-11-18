package com.forsteri.unlimitedfluidity.core.fluidbehaviors.fluidfog;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.IFluidBehavior;
import com.forsteri.unlimitedfluidity.util.Api;
import com.forsteri.unlimitedfluidity.util.Triplet;

import java.util.Optional;

/**
 * <p>Api for creating fluid behavior making custom fluid fog without complicated events</p>
 * <p>This is a interface for {@link FluidFogBehavior} for allowance of further extending</p>
 * @since       2.0
 */
public interface IFluidFogBehavior extends IFluidBehavior {
    /**
     * @return      fog color of the fluid, in RGB percentage, won't override anything if value empty
     * @since       2.0
     */
    @Api
    Triplet<Optional<Float>, Optional<Float>, Optional<Float>> fogColor();

    /**
     * @return      scale the far plane distance, won't override anything if empty
     * @since       2.0
     */
    @Api
    Optional<Float> scaleFarPlaneDistance();
}
