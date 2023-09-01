package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate;
import com.forsteri.unlimitedfluidity.util.Api;

@Api
public record HydrateBehavior(boolean canHydrate) implements IHydrateBehavior {
    @Api
    public HydrateBehavior() {
        this(false);
    }
}
