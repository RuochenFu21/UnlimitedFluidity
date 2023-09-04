package com.forsteri.unlimitedfluidity.entry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface FluidityTags<T> {
    ResourceLocation getLocation();
    ResourceKey<? extends Registry<T>> getRegistry();

    default TagKey<T> getTag() {
        return TagKey.create(getRegistry(), getLocation());
    }

    enum FluidityBlockTags implements FluidityTags<Block> {
        HAS_FIRE("has_fire");
        FluidityBlockTags(String location) {
            this.location = new ResourceLocation("unlimitedfluidity", location);
        }

        FluidityBlockTags(String namespace, String location) {
            this.location = new ResourceLocation(namespace, location);
        }

        private final ResourceLocation location;

        @Override
        public ResourceLocation getLocation() {
            return location;
        }

        @Override
        public ResourceKey<? extends Registry<Block>> getRegistry() {
            return Registry.BLOCK_REGISTRY;
        }
    }
}
