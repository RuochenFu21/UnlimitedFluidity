package com.forsteri.unlimitedfluidity.debug;

import com.forsteri.unlimitedfluidity.UnlimitedFluidity;
import com.forsteri.unlimitedfluidity.core.flowinggas.FlowingGas;
import com.forsteri.unlimitedfluidity.core.flowinggas.GasBlock;
import com.forsteri.unlimitedfluidity.core.flowinggas.command.GasTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class FluidityDebugRegistry {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, UnlimitedFluidity.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, UnlimitedFluidity.MOD_ID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, UnlimitedFluidity.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UnlimitedFluidity.MOD_ID);

    public static final DeferredRegister<ArgumentTypeInfo<?,?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, UnlimitedFluidity.MOD_ID);
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");

    public static RegistryObject<FluidType>
            DEBUG_FLUID_TYPE = FLUID_TYPES.register("debug_fluid_type", () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.minecraft.air")
                    .motionScale(1D)
                    .canPushEntity(false)
                    .canSwim(false)
                    .canDrown(false)
                    .fallDistanceModifier(1F)
                    .pathType(null)
                    .adjacentPathType(null)
                    .density(5)
                    .temperature(0)
                    .viscosity(0)) {
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                public ResourceLocation getStillTexture() {
                    return WATER_STILL_RL;
                }

                public ResourceLocation getFlowingTexture() {
                    return WATER_FLOWING_RL;
                }
            });
        }
    });

    public static RegistryObject<FlowingFluid>
            DEBUG_FLUID = FluidityDebugRegistry.FLUIDS.register("debug_fluid", () -> new FlowingGas.Source(FluidityDebugRegistry.DEBUG_FLUID_PROPERTY)),

            DEBUG_FLUID_FLOWING = FluidityDebugRegistry.FLUIDS.register("debug_fluid_flowing", () -> new FlowingGas.Flowing(FluidityDebugRegistry.DEBUG_FLUID_PROPERTY));

    public static ForgeFlowingFluid.Properties DEBUG_FLUID_PROPERTY = new ForgeFlowingFluid.Properties(
            () -> DEBUG_FLUID_TYPE.get(),
            () -> DEBUG_FLUID.get(),
            () -> DEBUG_FLUID_FLOWING.get())
            .block(() -> FluidityDebugRegistry.DEBUG_FLUID_BLOCK.get())
            .bucket(() -> FluidityDebugRegistry.DEBUG_FLUID_BUCKET.get());

    public static RegistryObject<LiquidBlock> DEBUG_FLUID_BLOCK = FluidityDebugRegistry.BLOCKS.register("debug_fluid_block",
            () -> new GasBlock(() -> FluidityDebugRegistry.DEBUG_FLUID.get(), BlockBehaviour.Properties.of()
                    .noCollission().replaceable().strength(100f).noLootTable()));

    public static RegistryObject<Item> DEBUG_FLUID_BUCKET = FluidityDebugRegistry.ITEMS.register("debug_fluid_bucket",
            () -> new BucketItem(FluidityDebugRegistry.DEBUG_FLUID,
                    new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ArgumentTypeInfos.registerByClass(GasTypeArgument.class, SingletonArgumentInfo.contextFree(GasTypeArgument::new));

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
    }
}
