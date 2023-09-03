package com.forsteri.unlimitedfluidity.core.fluidbehaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.List;
import java.util.function.Supplier;

/**
 * <p>Some fluid behavior might require the liquid block to be behaviorable</p>
 * @since       2.0
 */
public class BehaviorableLiquidBlock extends LiquidBlock implements IBehaviorable {

    public BehaviorableLiquidBlock(Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (getBehaviors().stream().anyMatch(behavior -> behavior.onNeighborChange(state, level, pos, neighbor)))
            return;

        super.onNeighborChange(state, level, pos, neighbor);
    }

    public List<IFluidBehavior> getBehaviors() {
        FlowingFluid fluid = this.getFluid();
        if (!(fluid instanceof BehaviorableFluid behaviorableFluid))
            throw new IllegalStateException("Fluid " + fluid + " is not a BehaviorableFluid, the block " + this + " cannot be a BehaviorableLiquidBlock");
        return behaviorableFluid.getBehaviors();
    }
}
