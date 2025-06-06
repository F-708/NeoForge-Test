package net.f708.realisticforging.datagen;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, RealisticForging.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.CARVEDDIAMONDORE);
        blockWithItem(ModBlocks.CARVEDDIAMONDORE2);
        blockWithItem(ModBlocks.CARVEDDIAMONDDEEPSLATE);
        blockWithItem(ModBlocks.CARVEDDIAMONDDEEPSLATE2);
        blockWithItem(ModBlocks.CARVEDEMERALDORE);
        blockWithItem(ModBlocks.CARVEDEMERALDORE2);
        blockWithItem(ModBlocks.CARVEDEMERALDDEEPSLATE);
        blockWithItem(ModBlocks.CARVEDEMERALDDEEPSLATE2);

    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
