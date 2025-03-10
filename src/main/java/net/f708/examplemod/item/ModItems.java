package net.f708.examplemod.item;

import net.f708.examplemod.ExampleMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);

    public static final DeferredItem<Item> HOTRAWIRONORE = ITEMS.register("hot_raw_iron_ore",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAWCLEANEDIRONORE = ITEMS.register("cleaned_raw_iron_ore",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAWIRONNUGGET = ITEMS.register("raw_iron_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CLEANEDRAWIRONNUGGET = ITEMS.register("cleaned_raw_iron_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HOTRAWIRONNUGGET = ITEMS.register("hot_raw_iron_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURERAWGOLDORE = ITEMS.register("pure_raw_gold_ore",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPERDUST = ITEMS.register("copper_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ROUGHDIAMOND = ITEMS.register("rough_diamond",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ROUGHCUTDIAMOND = ITEMS.register("rough_cut_diamond",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOLDEDDIAMOND = ITEMS.register("molded_diamond",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOLDEDDIAMOND2 = ITEMS.register("molded_diamond_2",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ROUGHEMERALD = ITEMS.register("rough_emerald",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CUTROUGHEMERALD = ITEMS.register("cut_rough_emerald",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NOTPOLISHEDEMERALD = ITEMS.register("not_polished_emerald",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> POLISHEDEMERALD = ITEMS.register("polished_emerald",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SMITHINGHAMMER = ITEMS.register("smithing_hammer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STONESMITHINGHAMMER = ITEMS.register("stone_smithing_hammer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODSMITHINGHAMMER = ITEMS.register("wood_smithing_hammer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WHEATBRUSH = ITEMS.register("wheat_brush",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TONGS = ITEMS.register("tongs",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TWOSTICKS = ITEMS.register("two_sticks",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BARSHAPEDMOLD = ITEMS.register("bar_shaped_mold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PAN = ITEMS.register("pan",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BIGPAN = ITEMS.register("big_pan",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODPESTLE = ITEMS.register("wood_pestle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CARVINGHAMMER = ITEMS.register("carving_hammer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> POINTCHISEL = ITEMS.register("point_chisel",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SANDPAPER = ITEMS.register("sandpaper",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
