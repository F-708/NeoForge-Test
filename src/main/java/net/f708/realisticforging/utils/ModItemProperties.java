package net.f708.realisticforging.utils;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.HOTRAWIRONORE.get(), ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "state"),
                ((stack, level, entity, seed) ->
                        switch (stack.get(ModDataComponents.FORGE_STATE)){
                        case 0 -> 0f;
                        case 1 -> 0.0f;
                        case 2 -> 0.1f;
                        case 3 -> 0.2f;
                        case 4 -> 0.3f;
                        case 5 -> 0.4f;
                        case 6 -> 0.5f;
                        case 7 -> 0.6f;
                        case 8 -> 0.7f;
                        case 9 -> 0.8f;
                        case 10 -> 0.9f;
                        case null -> 0f;
                        default ->
                                0.9f;
                }));

        ItemProperties.register(ModItems.ROUGHCUTDIAMOND.get(), ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "state"),
                ((stack, level, entity, seed) -> switch (stack.get(ModDataComponents.GRIND_STATE)) {
                    case 0 -> 0f;
                    case 1 -> 0.0f;
                    case 2 -> 0.1f;
                    case 3 -> 0.2f;
                    case 4 -> 0.3f;
                    case 5 -> 0.4f;
                    case 6 -> 0.5f;
                    case 7 -> 0.6f;
                    case 8 -> 0.7f;
                    case 9 -> 0.8f;
                    case 10 -> 0.9f;
                    case 11 -> 1f;
                    case null -> 0F;
                    default ->
                            throw new IllegalStateException("Unexpected value: " + stack.get(ModDataComponents.FORGE_STATE));
                }));

        ItemProperties.register(ModItems.TONGS.get(), ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "holding"),
                (((stack, level, entity, seed) -> {
                    float value = 0f;
                  if (stack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR) != Items.AIR){
                      value = 1f;
                  }
                  return value;
                })));

    }
}
