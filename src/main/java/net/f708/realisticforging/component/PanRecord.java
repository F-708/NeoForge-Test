//package net.f708.realisticforging.component;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.world.item.ItemStack;
//
//import java.util.List;
//
//public record PanRecord(List<ItemStack> items) {
//
//    public ItemStack getItems(List<ItemStack> items){
//        return items.stream().;
//    }
//
//    public static final Codec<PanRecord> BASIC_CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//                    ModDataComponents.CODEC.fieldOf("items").forGetter(PanRecord::items)
//            ).apply(instance, PanRecord::new)
//    );
//    public static final StreamCodec<RegistryFriendlyByteBuf, PanRecord> BASIC_STREAM_CODEC = StreamCodec.composite(
//            ItemStack.OPTIONAL_STREAM_CODEC, PanRecord::stack,
//            PanRecord::new
//    );
//}
