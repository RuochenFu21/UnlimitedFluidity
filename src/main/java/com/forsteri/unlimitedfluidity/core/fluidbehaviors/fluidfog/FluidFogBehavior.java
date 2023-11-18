package com.forsteri.unlimitedfluidity.core.fluidbehaviors.fluidfog;

import com.forsteri.unlimitedfluidity.util.Triplet;

import java.util.Optional;

/**
 * <p>Default fluid behavior implementation for creating fluid behavior making custom fluid fog without complicated events</p>
 * @since       2.0
 * @param fogColor the fog color of the fluid, in RGB percentage, won't override anything if value empty
 * @param scaleFarPlaneDistance the scale of the far plane distance, won't override anything if empty
 */
public record FluidFogBehavior(
        Triplet<Optional<Float>, Optional<Float>, Optional<Float>> fogColor,
        Optional<Float> scaleFarPlaneDistance
) implements IFluidFogBehavior {}
