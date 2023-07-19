package com.forsteri.unlimitedfluidity.mixin;

import com.forsteri.unlimitedfluidity.core.Viscosity;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import com.forsteri.unlimitedfluidity.core.Swimable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class MixinEntityMovement extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity{
    @Shadow public abstract void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_);

    @Shadow public Level level;

    @Shadow private Vec3 position;

    @Shadow public abstract boolean isInWater();

    protected MixinEntityMovement(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "checkInsideBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"))
    private void SlowdownInsideBlock(CallbackInfo ci) {
        if (level.getFluidState(new BlockPos(position)).getType() instanceof Viscosity fluid) {
            this.makeStuckInBlock(
                    level.getBlockState(new BlockPos(position)),
                    new Vec3(
                            1f/fluid.getViscosity(),
                            1f/fluid.getViscosity(),
                            1f/fluid.getViscosity()
                    )
            );
        }
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"))
    private boolean swimInWaterRedirect(Entity instance){
        if (level.getFluidState(new BlockPos(position)).getType() instanceof Swimable fluid && fluid.canSwim()) {
            return true;
        }
        return isInWater();
    }
}
