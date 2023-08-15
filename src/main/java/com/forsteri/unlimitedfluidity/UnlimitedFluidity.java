package com.forsteri.unlimitedfluidity;

import com.forsteri.unlimitedfluidity.core.flowingGas.FlowingGas;
import com.forsteri.unlimitedfluidity.core.flowingGas.GasMovementHandler;
import com.forsteri.unlimitedfluidity.core.flowingGas.command.GasTypeArgument;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.forsteri.unlimitedfluidity.core.ModParticles;
import com.forsteri.unlimitedfluidity.debug.DebugRegistry;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

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
        event.getDispatcher().register(Commands.literal(UnlimitedFluidity.MOD_ID)
                .then(Commands.literal("gas-cache")
                        .then(Commands.literal("clear")
                                .then(Commands.argument("gas", new GasTypeArgument())
                                        .requires(context -> context.hasPermission(2))
                                        .executes(context -> {
                                            GasMovementHandler.handlers.keySet().forEach(
                                                    pair -> {
                                                        // TODO: Check which one effects the most
                                                        GasMovementHandler.getOrCreate(pair.getFirst(), context.getArgument("gas", FlowingGas.class)).graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
                                                        FlowingGas.gasMovementMap.get(pair.getFirst()).clear();
                                                    }
                                            );
                                            context.getSource().sendSuccess(new TextComponent("Succeed to clear path finder cache for " + context.getArgument("gas", FlowingGas.class).getRegistryName()), false);
                                            return 0;
                                        })
                                )
                        )
                )
        );
    }
}
