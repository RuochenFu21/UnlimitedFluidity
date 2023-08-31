package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.hydrate.farmhydration.farmlandhydration.IFarmlandHydrationFluidBehavior;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmBlock.class)
public class MixinFarmBlock {
    @Redirect(method = "isNearWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean canSolidify(net.minecraft.world.level.material.FluidState fluidState, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tagKey) {
        IFarmlandHydrationFluidBehavior behavior;
        if (fluidState.getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(IFarmlandHydrationFluidBehavior.class)) != null
                && behavior.canHydrateFarmland()) {
            return true;
        }
        return fluidState.is(tagKey);
    }
}
