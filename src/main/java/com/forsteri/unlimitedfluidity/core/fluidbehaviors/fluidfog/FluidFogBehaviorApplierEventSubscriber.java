package com.forsteri.unlimitedfluidity.core.fluidbehaviors.fluidfog;


import com.forsteri.unlimitedfluidity.core.fluidbehaviors.BehaviorableFluid;
import com.forsteri.unlimitedfluidity.util.Triplet;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class FluidFogBehaviorApplierEventSubscriber {
    @SubscribeEvent
    public static void getFogDensity(EntityViewRenderEvent.RenderFogEvent event) {
        Optional<List<FluidFogBehavior>> optionalFluidFogBehaviors = getFluidFogBehaviors(event);

        if (optionalFluidFogBehaviors.isEmpty()) return;

        List<FluidFogBehavior> fluidFogBehaviors = optionalFluidFogBehaviors.get().stream().filter(fluidFogBehavior -> fluidFogBehavior.scaleFarPlaneDistance().isPresent()).toList();

        if (fluidFogBehaviors.isEmpty()) return;

        Optional<Float> scaleFarPlaneDistance = fluidFogBehaviors.get(fluidFogBehaviors.size() - 1).scaleFarPlaneDistance();

        if (scaleFarPlaneDistance.isEmpty()) return;

        event.scaleFarPlaneDistance(scaleFarPlaneDistance.get());
    }

    @SubscribeEvent
    public static void getFogColor(EntityViewRenderEvent.FogColors event) {
        Optional<List<FluidFogBehavior>> optionalFluidFogBehaviors = getFluidFogBehaviors(event);

        if (optionalFluidFogBehaviors.isEmpty()) return;

        List<FluidFogBehavior> fluidFogBehaviors = optionalFluidFogBehaviors.get().stream().filter(fluidFogBehavior ->
                fluidFogBehavior.fogColor().first().isPresent()
             || fluidFogBehavior.fogColor().second().isPresent()
             || fluidFogBehavior.fogColor().third().isPresent()
        ).toList();

        if (fluidFogBehaviors.isEmpty()) return;

        Triplet<Optional<Float>, Optional<Float>, Optional<Float>> color = fluidFogBehaviors.get(fluidFogBehaviors.size() - 1).fogColor();

        for (int i = 0; i < 3; i++) {
            Optional<Float> colorValue = switch (i) {
                case 0 -> color.first();
                case 1 -> color.second();
                case 2 -> color.third();
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };

            if (colorValue.isEmpty()) continue;

            switch (i) {
                case 0 -> event.setRed(colorValue.get());
                case 1 -> event.setGreen(colorValue.get());
                case 2 -> event.setBlue(colorValue.get());
            }
        }
    }

    private static Optional<List<FluidFogBehavior>> getFluidFogBehaviors(EntityViewRenderEvent event) {
        Camera camera = event.getCamera();
        Level level = Minecraft.getInstance().level;

        assert level != null;

        BlockPos blockPos = camera.getBlockPosition();
        FluidState fluidState = level.getFluidState(blockPos);
        if (camera.getPosition().y >= blockPos.getY() + fluidState.getHeight(level, blockPos))
            return Optional.empty();

        Fluid fluid = fluidState.getType();

        if (!(fluid instanceof BehaviorableFluid behaviorableFluid)) return Optional.empty();

        List<FluidFogBehavior> fluidFogBehaviors = behaviorableFluid.getBehavior(FluidFogBehavior.class);

        if (fluidFogBehaviors.isEmpty()) return Optional.empty();

        return Optional.of(fluidFogBehaviors);
    }
}