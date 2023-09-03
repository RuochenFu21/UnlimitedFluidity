package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.agriculturalhydration.sugarcanehydration.ISugarCaneHydrationFluidBehavior;
import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SugarCaneBlock.class)
public class MixinSugarCaneBlock {
    @Redirect(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean canSolidify(net.minecraft.world.level.material.FluidState fluidState, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tagKey) {
        List<ISugarCaneHydrationFluidBehavior> behavior;
        if (fluidState.getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(ISugarCaneHydrationFluidBehavior.class)) != null
                && behavior.stream().anyMatch(iter -> iter != null && iter.canHydrateSugarCane())) {
            return true;
        }
        return fluidState.is(tagKey);
    }
}
