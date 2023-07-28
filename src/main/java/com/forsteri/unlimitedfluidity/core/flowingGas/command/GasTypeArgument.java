package com.forsteri.unlimitedfluidity.core.flowingGas.command;

import com.forsteri.unlimitedfluidity.core.flowingGas.FlowingGas;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class GasTypeArgument implements ArgumentType<FlowingGas> {
    @Override
    public FlowingGas parse(StringReader reader) throws CommandSyntaxException {
        String gasName = ResourceLocation.read(reader).toString();
        if (
                !ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(gasName))
                || !(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(gasName)) instanceof FlowingGas flowingGas)
        )
            throw new SimpleCommandExceptionType(() -> "Unknown gas type: " + gasName).create();
        return flowingGas;
    }
}
