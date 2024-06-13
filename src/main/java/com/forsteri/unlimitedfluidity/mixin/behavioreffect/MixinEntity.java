package com.forsteri.unlimitedfluidity.mixin.behavioreffect;

import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity{
    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    protected MixinEntity(Class<Entity> baseClass, boolean isLazy) {
        super(baseClass, isLazy);
    }

    // TODO: Fuck this doesn't work, lazy to figure, if you are reading this, maybe help me
    /**
    @Shadow public abstract void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_);

    @Shadow public Level level;

    @Shadow private Vec3 position;

    @Shadow public abstract boolean isInWater();

    @Shadow public abstract @NotNull AABB getBoundingBox();

    @Shadow public abstract Vec3 position();

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "checkInsideBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"))
    private void SlowdownInsideBlock(CallbackInfo ci) {
        List<IViscosityFluidBehavior> behavior;

        BlockPos blockPos = new BlockPos(
                (int) position().x(),
                (int) position().y(),
                (int) position().z()
        );

        if (level.getFluidState(blockPos).getType() instanceof BehaviorableFluid fluid
        && (behavior = fluid.getBehavior(IViscosityFluidBehavior.class)) != null) {
            double speedMultiplier = behavior.stream().map(iter -> iter.viscosity()/behavior.size()).reduce(0, Integer::sum);
            this.makeStuckInBlock(
                    level.getBlockState(blockPos),
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

        BlockPos blockPos = new BlockPos(
                (int) position().x(),
                (int) position().y(),
                (int) position().z()
        );

        if (level.getFluidState(blockPos).getType() instanceof BehaviorableFluid fluid
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

    @ModifyVariable(method = "updateFluidHeightAndDoFluidPushing(Ljava/util/function/Predicate;)V", at = @At(value = "STORE"))
    private FluidState updateFluidHeightAndDoFluidPushing(FluidState original) {
        if ((original.getType() instanceof BehaviorableFluid behaviorableFluid)
                && !behaviorableFluid.getBehavior(IPushlessFluidBehavior.class).isEmpty())
            return Fluids.EMPTY.defaultFluidState();
        return original;
    }
    **/
}
