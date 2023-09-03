package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.sponging.ISpongingFluidBehavior;
import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SpongeBlock.class)
public class MixinSponge {
    @Redirect(method = "removeWaterBreadthFirstSearch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    public boolean removeCustomLiquidSponging(net.minecraft.world.level.material.FluidState fluidState, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tagKey) {
        List<ISpongingFluidBehavior> behavior;
        if (fluidState.getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(ISpongingFluidBehavior.class)) != null
                && behavior.stream().anyMatch(iter -> iter != null && iter.canSponge())) {
            return true;
        }
        return fluidState.is(tagKey);
    }
}
