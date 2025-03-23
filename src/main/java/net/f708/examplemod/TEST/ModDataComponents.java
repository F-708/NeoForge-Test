package net.f708.examplemod.TEST;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final Codec<ForgeStateRecord> FORGE_STATE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("forgestate").forGetter(ForgeStateRecord::forgestate)
            ).apply(instance, ForgeStateRecord::new));

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "examplemod");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ForgeStateRecord>> FORGE_STATE = REGISTRAR.registerComponentType(
            "forgestate",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FORGE_STATE_CODEC)
    );

    public static void applyModDataComponent(ItemStack stack, String state){
        if (stack instanceof MutableDataComponentHolder mutableStack){
            mutableStack.set(ModDataComponents.FORGE_STATE, new ForgeStateRecord(state));
    }
}

    public static void register(IEventBus modEventBus) {
        REGISTRAR.register(modEventBus);
    }
}

