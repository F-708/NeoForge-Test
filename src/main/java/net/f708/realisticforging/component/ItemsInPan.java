package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemsInPan(ItemStackListRecord record, boolean melt) {

    @Override
    public boolean melt() {
        return melt;
    }

    @Override
    public ItemStackListRecord record() {
        return record;
    }


//    public void setResultAsDataComponent(ItemStack stack, ItemsInPan record){
//        stack.set(ModDataComponents.ITEMS_IN_PAN, record);
//    }

    public static final Codec<ItemsInPan> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStackListRecord.CODEC.fieldOf("itemStack").forGetter(ItemsInPan::record),
                    Codec.BOOL.fieldOf("melt").forGetter(ItemsInPan::melt)
            ).apply(instance, ItemsInPan::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemsInPan> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStackListRecord.STREAM_CODEC, ItemsInPan::record,
                    ByteBufCodecs.BOOL, ItemsInPan::melt,
                    ItemsInPan::new
            );


}
