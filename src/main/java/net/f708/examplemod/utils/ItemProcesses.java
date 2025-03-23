package net.f708.examplemod.utils;

import net.f708.examplemod.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemProcesses {
    public static Map<Item, Item> FORGING_MAP = Map.of(
            ModItems.PICKEDHOTRAWIRON1.get(), ModItems.PICKEDHOTRAWIRON2.get(),
            ModItems.PICKEDHOTRAWIRON2.get(), ModItems.PICKEDHOTRAWIRON3.get(),
            ModItems.PICKEDHOTRAWIRON3.get(), ModItems.PICKEDHOTRAWIRON4.get(),
            ModItems.PICKEDHOTRAWIRON4.get(), ModItems.PICKEDHOTIRONINGOT.get(),
            ModItems.PICKEDHOTIRONINGOT.get(), ModItems.PICKEDHOTRAWIRON5.get(),
            ModItems.PICKEDHOTRAWIRON5.get(), ModItems.PICKEDHOTRAWIRON6.get(),
            ModItems.PICKEDHOTRAWIRON6.get(), ModItems.PICKEDHOTIRONSHEET.get(),
            ModItems.PICKEDHOTIRONSHEET.get(), ModItems.PICKEDHOTRAWIRON3.get()
    );
    }

