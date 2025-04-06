package net.f708.examplemod.utils;

import net.f708.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks{



            private static TagKey<Block> createTag (String name) {
                return BlockTags.create(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name));
            }
    }
    public static class Items{
        // If one in inventory - damage
        public static final TagKey<Item> VERY_HOT_ITEM = createTag("very_hot_item");
        // If more than 2 in inventory - damage
        public static final TagKey<Item> HOT_ITEM = createTag("hot_item");

        public static final TagKey<Item> HAMMER_ITEM = createTag("hammer_item");


            private static TagKey<Item> createTag (String name){
                return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name));
            }
    }
}
