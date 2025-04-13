package net.f708.examplemod.utils;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.PICKEDHOTRAWIRON.get(), ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "state"),
                ((stack, level, entity, seed) -> switch (stack.get(ModDataComponents.FORGE_STATE)){
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
                    case null -> 0.0F;
                    default ->
                            throw new IllegalStateException("Unexpected value: " + stack.get(ModDataComponents.FORGE_STATE));
                }));
    }
}
