package net.f708.realisticforging.datagen;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, RealisticForging.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getTag()
                .add(ModItems.HOTRAWIRONORE.get())
                .add(ModItems.HOTRAWIRONNUGGET.get())
                .add(ModItems.RAWCLEANEDIRONORE.get());
        tag(ModTags.Items.VERY_HOT_ITEM)
                .add(ModItems.HOTRAWIRONORE.get());
    }

    private @NotNull IntrinsicTagAppender<Item> getTag() {
        return tag(ModTags.Items.PICKABLE_WITH_TONGS);
    }
}
