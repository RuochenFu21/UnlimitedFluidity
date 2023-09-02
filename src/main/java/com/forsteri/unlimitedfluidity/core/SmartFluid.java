package com.forsteri.unlimitedfluidity.core;

import com.forsteri.unlimitedfluidity.util.Api;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Simplifies the process of making fluid</p>
 * <li>Built in the required redundant {@link FlowingFluid#LEVEL} fluid state definition/default value</li>
 * <li>Built in the required redundant {@link Source#getAmount(FluidState)} that always returns 8</li>
 * <li>Built in the required redundant {@link Source#getAmount(FluidState)} that always returns the value of {@link FlowingFluid#LEVEL} property</li>
 * @since      1.0
 */
@Api
public abstract class SmartFluid extends ForgeFlowingFluid {
    protected SmartFluid(Properties properties) {
        super(properties);

        if (this.isSource(this.defaultFluidState())) {
            this.registerDefaultState(this.getStateDefinition().any()
            );
        } else {
            this.registerDefaultState(this.getStateDefinition().any()
                    .setValue(LEVEL, 7)
            );
        }
    }

    protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        if (!this.isSource(this.defaultFluidState()))
            builder.add(LEVEL);
    }

    /**
     * @return whether the fluid state is source
     */
    @Api
    protected abstract boolean isSource();

    @Override
    public final boolean isSource(@NotNull FluidState p_76140_) {
        return isSource();
    }
}
