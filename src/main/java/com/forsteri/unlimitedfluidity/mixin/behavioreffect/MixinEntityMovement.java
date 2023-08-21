package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming.ISwimmingFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity.IViscosityFluidBehavior;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming.SwimmingFluidBehaviorImpl;
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
        IViscosityFluidBehavior behavior;

        if (level.getFluidState(new BlockPos(position)).getType() instanceof BehaviorableFluid fluid
        && (behavior = fluid.getBehavior(IViscosityFluidBehavior.class)) != null) {
            this.makeStuckInBlock(
                    level.getBlockState(new BlockPos(position)),
                    new Vec3(
                            1f/behavior.viscosity(),
                            1f/behavior.viscosity(),
                            1f/behavior.viscosity()
                    )
            );
        }
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"))
    private boolean swimInWaterRedirect(Entity instance){
        ISwimmingFluidBehavior behavior;

        if (level.getFluidState(new BlockPos(position)).getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(ISwimmingFluidBehavior.class)) != null
                && behavior.canSwim()) {
            return true;
        }

        return isInWater();
    }
}
