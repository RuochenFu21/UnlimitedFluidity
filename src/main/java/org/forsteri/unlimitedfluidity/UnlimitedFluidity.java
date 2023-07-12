package org.forsteri.unlimitedfluidity;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.forsteri.unlimitedfluidity.core.ModParticles;
import org.forsteri.unlimitedfluidity.debug.DebugRegistry;

@Mod(UnlimitedFluidity.MOD_ID)
public class UnlimitedFluidity {
    public static final String MOD_ID = "unlimitedfluidity";

    public UnlimitedFluidity() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModParticles.register(eventBus);
        DebugRegistry.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
