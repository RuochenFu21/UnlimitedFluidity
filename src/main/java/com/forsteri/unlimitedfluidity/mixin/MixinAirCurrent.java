package com.forsteri.unlimitedfluidity.mixin;

import com.forsteri.unlimitedfluidity.core.flowinggas.FlowingGas;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = AirCurrent.class, remap = false)
public class MixinAirCurrent {
    @Shadow @Final public IAirCurrentSource source;

    @Shadow public AABB bounds;

    @Shadow public Direction direction;

    @Shadow public boolean pushing;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        unlimitedFluidity$tickAffectedGas();
    }

    @Unique
    protected void unlimitedFluidity$tickAffectedGas() {
        System.out.println(this);
        if (Objects.requireNonNull(source.getAirCurrentWorld()).getLevelData().getDayTime() % (11 - source.getSpeed() * 10 / 256) != 0)
            return;

        BlockPos.betweenClosedStream(bounds.expandTowards(-1, -1, -1)).forEach(pos -> {
//            if (source.getAirCurrentWorld().getBlockState(pos).isAir())
//                source.getAirCurrentWorld().setBlock(pos, (switch (direction) {
//                    case UP -> Blocks.DIAMOND_BLOCK;
//                    case DOWN -> Blocks.IRON_BLOCK;
//                    case NORTH -> Blocks.GOLD_BLOCK;
//                    case SOUTH -> Blocks.NETHERITE_BLOCK;
//                    case EAST -> Blocks.COAL_BLOCK;
//                    case WEST -> Blocks.PACKED_ICE;
//                }).defaultBlockState(), 7);
            Level level = source.getAirCurrentWorld();
            if (level.getBlockState(pos).getFluidState().isEmpty())
                return;
            if (!(level.getBlockState(pos).getFluidState().getType() instanceof FlowingGas gas))
                return;

            gas.getMovementHandler(level).flow(pos, pushing ? direction : direction.getOpposite());
            gas.getMovementHandler(level).tick(level.getLevelData().getDayTime());

        });
    }
}
