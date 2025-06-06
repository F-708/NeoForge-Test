package net.f708.realisticforging.datagen;

import net.f708.realisticforging.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.CARVEDDIAMONDORE.get());
        dropSelf(ModBlocks.CARVEDDIAMONDORE2.get());
        dropSelf(ModBlocks.CARVEDDIAMONDDEEPSLATE.get());
        dropSelf(ModBlocks.CARVEDDIAMONDDEEPSLATE2.get());
        dropSelf(ModBlocks.CARVEDEMERALDORE.get());
        dropSelf(ModBlocks.CARVEDEMERALDORE2.get());
        dropSelf(ModBlocks.CARVEDEMERALDDEEPSLATE.get());
        dropSelf(ModBlocks.CARVEDEMERALDDEEPSLATE2.get());

    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
