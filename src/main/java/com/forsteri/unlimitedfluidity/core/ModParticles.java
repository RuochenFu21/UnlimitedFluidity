package com.forsteri.unlimitedfluidity.core;

import com.forsteri.unlimitedfluidity.UnlimitedFluidity;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, UnlimitedFluidity.MOD_ID);



    public static void register(IEventBus modEventBus) {
        PARTICLES.register(modEventBus);
    }
}
