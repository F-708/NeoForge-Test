package net.f708.realisticforging.attributes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.w3c.dom.Attr;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            BuiltInRegistries.ATTRIBUTE, "realisticforging");

    public static final Holder<Attribute> FORGING_ATTRIBUTE = ATTRIBUTES.register("forging_attribute", () -> new RangedAttribute(
            "attributes.realisticforging.forging_attribute",
            1,
            0, 2
    ));
    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }


}
