package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.onTouch.FluidEntityInteractionHandler;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.swimming.ISwimmingFluidBehavior;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.viscosity.IViscosityFluidBehavior;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity{
    @Shadow public abstract void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_);

    @Shadow public Level level;

    @Shadow private Vec3 position;

    @Shadow public abstract boolean isInWater();

    @Shadow public abstract @NotNull AABB getBoundingBox();

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "checkInsideBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"))
    private void SlowdownInsideBlock(CallbackInfo ci) {
        List<IViscosityFluidBehavior> behavior;

        if (level.getFluidState(new BlockPos(position)).getType() instanceof BehaviorableFluid fluid
        && (behavior = fluid.getBehavior(IViscosityFluidBehavior.class)) != null) {
            double speedMultiplier = behavior.stream().map(iter -> iter.viscosity()/behavior.size()).reduce(0, Integer::sum);
            this.makeStuckInBlock(
                    level.getBlockState(new BlockPos(position)),
                    new Vec3(
                            speedMultiplier,
                            speedMultiplier,
                            speedMultiplier
                    )
            );
        }
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"))
    private boolean swimInWaterRedirect(Entity instance){
        List<ISwimmingFluidBehavior> behavior;

        if (level.getFluidState(new BlockPos(position)).getType() instanceof BehaviorableFluid fluid
                && (behavior = fluid.getBehavior(ISwimmingFluidBehavior.class)) != null
                && behavior.stream().anyMatch(iter -> iter != null && iter.canSwim())) {
            return true;
        }

        return isInWater();
    }

    @Inject(method = "baseTick", at = @At(value = "TAIL"))
    private void tick(CallbackInfo ci) {
        // TODO: Fluid Lift Behavior

        FluidEntityInteractionHandler.handleInteraction((Entity) (Object) this);
    }
}
