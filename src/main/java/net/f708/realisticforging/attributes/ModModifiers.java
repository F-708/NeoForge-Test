package net.f708.realisticforging.attributes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ModModifiers {

    public static AttributeModifier getSlowModifier(){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("realisticforging", "slow_down_player");
        return new AttributeModifier(
                id,
                -0.6,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
    public static AttributeModifier getBlockRangeModifier(){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("realisticforging", "block_range");
        return new AttributeModifier(
                id,
                -0.65,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
