package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.concretehydration.IConcreteHydrationFluidBehavior;
import net.minecraft.world.level.block.ConcretePowderBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ConcretePowderBlock.class)
public class MixinConcretePowderBlock {
    @Redirect(method = "canSolidify", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean canSolidify(net.minecraft.world.level.material.FluidState fluidState, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tagKey) {
        List<IConcreteHydrationFluidBehavior> behavior;
        if (fluidState.getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(IConcreteHydrationFluidBehavior.class)) != null
                && behavior.stream().anyMatch(iter -> iter != null && iter.canHydrateConcrete())) {
            return true;
        }
        return fluidState.is(tagKey);
    }
}
