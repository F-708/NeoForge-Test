package net.f708.realisticforging.datagen;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.block.ModBlocks;
import net.f708.realisticforging.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RealisticForging.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .replace(false)
                .add(ModBlocks.CARVEDDIAMONDORE.get())
                .add(ModBlocks.CARVEDDIAMONDORE2.get())
                .add(ModBlocks.CARVEDEMERALDORE.get())
                .add(ModBlocks.CARVEDEMERALDORE2.get())
                .add(ModBlocks.CARVEDDIAMONDDEEPSLATE.get())
                .add(ModBlocks.CARVEDDIAMONDDEEPSLATE2.get())
                .add(ModBlocks.CARVEDEMERALDDEEPSLATE.get())
                .add(ModBlocks.CARVEDEMERALDDEEPSLATE2.get())
                .replace();


        tag(BlockTags.NEEDS_IRON_TOOL)
                .replace(false)
                .add(ModBlocks.CARVEDDIAMONDORE.get())
                .add(ModBlocks.CARVEDDIAMONDORE2.get())
                .add(ModBlocks.CARVEDEMERALDORE.get())
                .add(ModBlocks.CARVEDEMERALDORE2.get())
                .add(ModBlocks.CARVEDDIAMONDDEEPSLATE.get())
                .add(ModBlocks.CARVEDDIAMONDDEEPSLATE2.get())
                .add(ModBlocks.CARVEDEMERALDDEEPSLATE.get())
                .add(ModBlocks.CARVEDEMERALDDEEPSLATE2.get())
                .replace(false);

        tag(ModTags.Blocks.FORGEABLE_BLOCK)
                .replace(false)
                .add(Blocks.ANVIL)
                .add(Blocks.CHIPPED_ANVIL)
                .add(Blocks.DAMAGED_ANVIL)
                .add(Blocks.SMITHING_TABLE)
                .replace(false);

    }
}
