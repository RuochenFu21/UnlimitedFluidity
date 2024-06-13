package com.forsteri.unlimitedfluidity.mixin;

import com.forsteri.unlimitedfluidity.core.flowinggas.CloudParticleMarkingAccess;
import com.forsteri.unlimitedfluidity.core.flowinggas.FlowingGas;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCloudParticle.class)
public abstract class MixinPlayerCloudParticle extends TextureSheetParticle implements CloudParticleMarkingAccess {
    protected MixinPlayerCloudParticle(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void unlimitedFluidity$checkGasStillExists(CallbackInfo ci) {
        if (unlimitedFluidity$isGasParticle) {
            BlockPos pos = new BlockPos(
                    (int) this.x,
                    (int) this.y,
                    (int) this.z
            );
            BlockState state = level.getBlockState(pos);
            if (!state.getFluidState().is(unlimitedFluidity$gasParticle))
                remove();
        }
    }

    @Unique
    private boolean unlimitedFluidity$isGasParticle = false;

    @Unique
    private FlowingGas unlimitedFluidity$gasParticle = null;
    @Override
    public void unlimitedFluidity$markAsGasParticle(FlowingGas fluid) {
        unlimitedFluidity$isGasParticle = true;
        unlimitedFluidity$gasParticle = fluid;
    }
}
