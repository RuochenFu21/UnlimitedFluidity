package com.forsteri.unlimitedfluidity;

import com.forsteri.unlimitedfluidity.core.flowinggas.FlowingGas;
import com.forsteri.unlimitedfluidity.core.flowinggas.GasMovementHandler;
import com.forsteri.unlimitedfluidity.core.flowinggas.command.GasTypeArgument;
import com.forsteri.unlimitedfluidity.core.fluidbehaviors.ontouch.FluidEntityInteractionHandler;
import com.forsteri.unlimitedfluidity.debug.FluidityDebugRegistry;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.List;

@Mod(UnlimitedFluidity.MOD_ID)
public class UnlimitedFluidity {
    public static final String MOD_ID = "unlimitedfluidity";

    public UnlimitedFluidity() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        FluidityDebugRegistry.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTickEvent(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        GasMovementHandler.handlers.forEach(
                (pair, handler) -> {
                    SimpleWeightedGraph<BlockPos, DefaultWeightedEdge> graph = handler.getGraph();
                    List<DefaultWeightedEdge> removingEdges = new ArrayList<>();
                    graph.edgeSet().forEach(edge -> {
                                graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) - 1d/(20 * 60));
                                double weight = graph.getEdgeWeight(edge);
                                if (weight <= 0)
                                    removingEdges.add(edge);
                    });

                    removingEdges.forEach(graph::removeEdge);

                    List<BlockPos> removingVertexes = new ArrayList<>();

                    graph.vertexSet().forEach(vertex -> {
                        if (graph.edgesOf(vertex).isEmpty())
                            removingVertexes.add(vertex);
                    });

                    removingVertexes.forEach(graph::removeVertex);
                }
        );

        event.getServer().getAllLevels().forEach(
                serverLevel -> serverLevel.getEntities().getAll().forEach(
                        FluidEntityInteractionHandler::handleInteraction
                )
        );
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
                                                        GasMovementHandler.getOrCreate(pair.getFirst(), context.getArgument("gas", FlowingGas.class)).setGraph(new SimpleWeightedGraph<>(DefaultWeightedEdge.class));
                                                        FlowingGas.gasMovementMap.get(pair.getFirst()).clear();
                                                    }
                                            );
                                            context.getSource().sendSuccess(() -> Components.literal("Succeed to clear path finder cache"), false);
                                            return 0;
                                        })
                                )
                        )
                )
        );
    }
}
