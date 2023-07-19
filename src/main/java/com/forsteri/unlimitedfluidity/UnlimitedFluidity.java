package com.forsteri.unlimitedfluidity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.forsteri.unlimitedfluidity.core.ModParticles;
import com.forsteri.unlimitedfluidity.debug.DebugRegistry;

@Mod(UnlimitedFluidity.MOD_ID)
public class UnlimitedFluidity {
    public static final String MOD_ID = "unlimitedfluidity";

    public UnlimitedFluidity() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModParticles.register(eventBus);
        DebugRegistry.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        System.out.println("Registering commands");
    }
}
