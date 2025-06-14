package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.f708.realisticforging.RealisticForging;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(RealisticForging.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORGE_STATE = register("forge_state",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GRIND_STATE = register("grind_state",
            integerBuilder -> integerBuilder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackRecord>> ITEM_IN_TONGS = register("item_in_tongs",
            builder -> builder.persistent(ItemStackRecord.BASIC_CODEC)
                    .networkSynchronized(ItemStackRecord.BASIC_STREAM_CODEC)
    );


    public static final Codec<Holder<Item>> ITEM_AIR_CODEC = BuiltInRegistries.ITEM.holderByNameCodec();
    public static final Codec<ItemStack> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    p_347288_ -> p_347288_.group(
                                    ITEM_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                                    ExtraCodecs.intRange(0, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                                    DataComponentPatch.CODEC
                                            .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                            .forGetter(p_330103_ -> {
                                                if (p_330103_.getComponents() instanceof PatchedDataComponentMap map) {
                                                    return map.asPatch();
                                                }
                                                return DataComponentPatch.EMPTY;
                                            })
                            )
                            .apply(p_347288_, ItemStack::new)
            )
    );


//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> ITEM_IN_TONGS = register("item_in_tongs",
//            builder -> builder.persistent(CODEC));

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Item>> ITEM_IN_TONGS = register("item_in_tongs",
//            builder -> builder.persistent(BuiltInRegistries.ITEM.byNameCodec())
//                    .networkSynchronized(ByteBufCodecs.registry(Registries.ITEM)));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return DATA_COMPONENT_TYPES.register(name, ()-> builderOperator.apply(DataComponentType.builder())
                .build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
