package com.forsteri.unlimitedfluidity.debug;

import com.forsteri.unlimitedfluidity.UnlimitedFluidity;
import com.forsteri.unlimitedfluidity.core.flowingGas.FlowingGas;
import com.forsteri.unlimitedfluidity.core.flowingGas.GasBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DebugRegistry {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, UnlimitedFluidity.MOD_ID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, UnlimitedFluidity.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UnlimitedFluidity.MOD_ID);
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

    public static RegistryObject<FlowingFluid>
            DEBUG_FLUID = DebugRegistry.FLUIDS.register("debug_fluid", () -> new FlowingGas.Source(DebugRegistry.DEBUG_FLUID_PROPERTY)),

            DEBUG_FLUID_FLOWING = DebugRegistry.FLUIDS.register("debug_fluid_flowing", () -> new FlowingGas.Flowing(DebugRegistry.DEBUG_FLUID_PROPERTY));

    public static ForgeFlowingFluid.Properties DEBUG_FLUID_PROPERTY = new ForgeFlowingFluid.Properties(
            () -> DEBUG_FLUID.get(),
            () -> DEBUG_FLUID_FLOWING.get(),
            FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL)
                    .density(15).luminosity(2).viscosity(5).overlay(WATER_OVERLAY_RL)
                    .color(0xbffcba03)).slopeFindDistance(0).levelDecreasePerBlock(0).tickRate(1)
            .block(() -> DebugRegistry.DEBUG_FLUID_BLOCK.get()).bucket(() -> DebugRegistry.DEBUG_FLUID_BUCKET.get()
            );

    public static RegistryObject<LiquidBlock> DEBUG_FLUID_BLOCK = DebugRegistry.BLOCKS.register("debug_fluid_block",
            () -> new GasBlock(() -> DebugRegistry.DEBUG_FLUID.get(), BlockBehaviour.Properties.of(Material.WATER)
                    .noCollission().strength(100f).noDrops()));

    public static RegistryObject<Item> DEBUG_FLUID_BUCKET = DebugRegistry.ITEMS.register("debug_fluid_bucket",
            () -> new BucketItem(DebugRegistry.DEBUG_FLUID,
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
