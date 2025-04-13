package net.f708.examplemod.attributes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SlowDownModifier {

    public static AttributeModifier get(){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("examplemod", "slow_down_player");
        return new AttributeModifier(
                id,
                -0.6,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
