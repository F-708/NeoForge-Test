package net.f708.realisticforging.datagen;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RealisticForging.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.RAWCLEANEDIRONORE.get());
        basicItem(ModItems.RAWIRONNUGGET.get());
        basicItem(ModItems.CLEANEDRAWIRONNUGGET.get());
        basicItem(ModItems.HOTRAWIRONNUGGET.get());
        basicItem(ModItems.PURERAWGOLDORE.get());
        basicItem(ModItems.ROUGHDIAMOND.get());
        basicItem(ModItems.MOLDEDDIAMOND.get());
        basicItem(ModItems.MOLDEDDIAMOND2.get());
        basicItem(ModItems.ROUGHEMERALD.get());
        basicItem(ModItems.CUTROUGHEMERALD.get());
        basicItem(ModItems.NOTPOLISHEDEMERALD.get());
        basicItem(ModItems.POLISHEDEMERALD.get());
        basicItem(ModItems.BARSHAPEDMOLD.get());
        basicItem(ModItems.PAN.get());
        basicItem(ModItems.BIGPAN.get());
        basicItem(ModItems.TONGS.get());
        basicItem(ModItems.TWOSTICKS.get());


        handheldItem(ModItems.SMITHINGHAMMER.get());
        handheldItem(ModItems.STONESMITHINGHAMMER.get());
        handheldItem(ModItems.WOODSMITHINGHAMMER.get());
        handheldItem(ModItems.CARVINGHAMMER.get());
        handheldItem(ModItems.POINTCHISEL.get());
        handheldItem(ModItems.IRON_SLEDGEHAMMER.get());
        handheldItem(ModItems.DIAMOND_SLEDGEHAMMER.get());
        handheldItem(ModItems.NETHERITE_SLEDGEHAMMER.get());


    }
}
