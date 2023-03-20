package org.forsteri123.unlimitedfluidity.mixin;

import net.minecraft.world.level.block.SpongeBlock;
import org.forsteri123.unlimitedfluidity.core.Unspongable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpongeBlock.class)
public class MixinSponge {
    @Redirect(method = "removeWaterBreadthFirstSearch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    public boolean removeCustomLiquidSponging(net.minecraft.world.level.material.FluidState fluidState, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tagKey) {
        if (fluidState.getType() instanceof Unspongable fluid && !fluid.spongable()) {
            return false;
        }
        return fluidState.is(tagKey);
    }
}
