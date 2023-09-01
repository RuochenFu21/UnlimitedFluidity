package com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration;

import com.forsteri.unlimitedfluidity.util.Api;

@Api
public record FarmHydrationFluidBehaviorImpl(boolean canHydrateFarm) implements IFarmHydrationFluidBehavior { }
