package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;


public record ItemStackRecord(ItemStack itemStack) {


    public static void clearItemStackFromDataComponent(ItemStack stack){
        stack.remove(ModDataComponents.ITEM_IN_TONGS);
    }

    public static void setItemStackIntoDataComponent(ItemStack thisItem, ItemStack intoThisItem){
            ItemStackRecord record = new ItemStackRecord(thisItem.copy());
            intoThisItem.set(ModDataComponents.ITEM_IN_TONGS, record);
    }


    public static ItemStack getStackFromDataComponent(ItemStack stack){
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStackRecord record = stack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, new ItemStackRecord(ItemStack.EMPTY));
        if (record.itemStack() instanceof ItemStack anotherStack){
              itemStack = anotherStack.copyWithCount(1);
        }
        return itemStack;
    }

    public static void setForgingStateDefault(ItemStack stack){
        ItemStack gotStack = getStackFromDataComponent(stack);
        gotStack.set(ModDataComponents.FORGE_STATE, 1);
        setItemStackIntoDataComponent(gotStack, stack);
    }

    public static void increaseForgingState(ItemStack stack, int amount){
        ItemStack gotStack = getStackFromDataComponent(stack);
        gotStack.set(ModDataComponents.FORGE_STATE, gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) + amount);
        setItemStackIntoDataComponent(gotStack, stack);
    }

    public static void descreaseForgingState(ItemStack stack, int amount){
        ItemStack gotStack = getStackFromDataComponent(stack);
        if (!(gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) - amount < 1)){
            gotStack.set(ModDataComponents.FORGE_STATE, gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) - amount);
        } else {
            gotStack.set(ModDataComponents.FORGE_STATE, 1);
        }
        setItemStackIntoDataComponent(gotStack, stack);
    }



    @Override
    public ItemStack itemStack() {
        return itemStack;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof ItemStackRecord ex
                    && this.itemStack == ex.itemStack;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.itemStack);
    }

    public static final Codec<ItemStackRecord> BASIC_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ModDataComponents.CODEC.fieldOf("itemStack").forGetter(ItemStackRecord::itemStack)
            ).apply(instance, ItemStackRecord::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackRecord> BASIC_STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ItemStackRecord::itemStack,
            ItemStackRecord::new
    );


    public static final Codec<Holder<Item>> ITEM_AIR_CODEC = BuiltInRegistries.ITEM.holderByNameCodec();
    public static final Codec<ItemStack> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    p_347288_ -> p_347288_.group(
                                    ITEM_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
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

}