package org.forsteri.unlimitedfluidity.core.flowingGas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GasBlock extends LiquidBlock {
    // TODO: Debug GasBlock and change all 8 into FlowingGas.MAX_AMOUNT

    public static final IntegerProperty DENSITY = FlowingGas.DENSITY;

    public GasBlock(Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
        this.registerDefaultState(this.stateDefinition.any().setValue(DENSITY, 1));
    }

    public @NotNull VoxelShape getCollisionShape(BlockState p_54760_, BlockGetter p_54761_, BlockPos p_54762_, CollisionContext p_54763_) {
        return p_54763_.isAbove(STABLE_SHAPE, p_54762_, true) && p_54760_.getValue(DENSITY) == 0 && p_54763_.canStandOnFluid(p_54761_.getFluidState(p_54762_.above()), p_54760_.getFluidState()) ? STABLE_SHAPE : Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54730_) {
        super.createBlockStateDefinition(p_54730_);
        p_54730_.add(DENSITY);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState pState) {
        int level = pState.getValue(DENSITY);
        return level == FlowingGas.MAX_AMOUNT ? getFluid().getSource(false) : getFluid().getFlowing(level, false);
    }
}
