package com.forsteri.unlimitedfluidity.mixin;

import com.forsteri.unlimitedfluidity.core.flowinggas.FlowingGas;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public abstract class MixinLiquidBlockRenderer {
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void tesselate(BlockAndTintGetter pTint, BlockPos pPos, VertexConsumer pConsumer, BlockState pState, FluidState pFluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!(pFluidState.getType() instanceof FlowingGas gas))
            return;

        FlowingGas.render(pTint, pPos, gas);
//        cir.setReturnValue(true);
    }
}
