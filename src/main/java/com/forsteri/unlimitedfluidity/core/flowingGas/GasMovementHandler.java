package com.forsteri.unlimitedfluidity.core.flowingGas;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GasMovementHandler {
    public static final Map<Pair<LevelAccessor, FlowingGas>, GasMovementHandler> handlers = new HashMap<>();

    private final LevelAccessor level;
    public static final int DEFAULT_VERTEX_WEIGHT = 128;

    private final FlowingGas source;

    public final Graph<Weighted<BlockPos>, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

    public GasMovementHandler(LevelAccessor level, FlowingGas source) {
        this.level = level;
        this.source = source;
    }

    public GasMovementHandler(Pair<LevelAccessor, FlowingGas> pair) {
        this(pair.getFirst(), pair.getSecond());
    }

    protected List<GasMovement> operations = new ArrayList<>();

    protected long lastTick = 0;

    public void tick(long tick) {
        if (tick >= lastTick) {
            lastTick = tick;
//            graph
        }

        while (!operations.isEmpty()) {
            GasMovement movement = operations.get(0);
            operations.remove(0);

            if (movement.operations.stream().anyMatch(operation ->
                    getDensity(operation.getFirst()) == -1 ||
                    getDensity(operation.getFirst()) + operation.getSecond() < 0
            )) continue;

            for (Pair<BlockPos, Integer> operation : movement.operations) {
                BlockPos pos = operation.getFirst();
                int resultDensity = getDensity(pos) + operation.getSecond();

                if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight()) continue;

                if (resultDensity == 0) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                } else if (resultDensity == FlowingGas.MAX_AMOUNT) {
                    level.setBlock(pos, source.getSource().defaultFluidState().createLegacyBlock(), 11);
                } else {
                    level.setBlock(pos, source.getFlowing(resultDensity, false).createLegacyBlock(), 11);
                }
            }
        }
    }

    protected int getDensity(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getFluidState().is(source.getSource())) return state.getValue(FlowingGas.DENSITY);
        if (state.getFluidState().is(source.getFlowing())) return state.getValue(FlowingGas.DENSITY);
        return level.getBlockState(pos).canBeReplaced(source) ? 0 : -1;
    }

    public void rise(BlockPos pos, int density) {
        move(pos, density, Direction.UP);
    }

    public void move(BlockPos pos, int density, Direction direction) {
        operations.add(GasMovement.create()
                .decrease(pos, density)
                .increase(pos.relative(direction), density));
    }

    public void increase(BlockPos pos, int density) {
        operations.add(GasMovement.create()
                .increase(pos, density));
    }

    private static class GasMovement {
        protected ArrayList<Pair<BlockPos, Integer>> operations = new ArrayList<>();

        public static GasMovement create() {
            return new GasMovement();
        }

        public GasMovement() {}

        public GasMovement increase(BlockPos pos, int density) {
            operations.add(new Pair<>(pos, density));
            return this;
        }

        public GasMovement decrease(BlockPos pos, int density) {
            operations.add(new Pair<>(pos, -density));
            return this;
        }
    }

    public static GasMovementHandler getOrCreate(LevelAccessor level, FlowingGas gas) {
        return handlers.computeIfAbsent(Pair.of(level, gas.getSource()), GasMovementHandler::new);
    }
}
