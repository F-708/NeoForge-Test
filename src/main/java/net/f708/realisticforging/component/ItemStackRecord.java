package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;


public record ItemStackRecord(ItemStack itemStack) {


    public static void clearItemStackFromDataComponent(ItemStack stack){
        stack.set(ModDataComponents.ITEM_IN_TONGS, new ItemStackRecord(Items.AIR.getDefaultInstance()));
    }


    public static void setItemStackIntoDataComponent(ItemStack thisItem, ItemStack intoThisItem){
        ItemStackRecord record = new ItemStackRecord(thisItem);
        intoThisItem.set(ModDataComponents.ITEM_IN_TONGS, record);
    }


    public static ItemStack getStackFromDataComponent(ItemStack stack){
        return (ItemStack) stack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR);
    }

    public static void setForgingStateDefault(ItemStack stack){
        ItemStack gotStack = getStackFromDataComponent(stack);
        gotStack.set(ModDataComponents.FORGE_STATE, 1);
        ItemStackRecord modifiedStack = new ItemStackRecord(gotStack);
        stack.set(ModDataComponents.ITEM_IN_TONGS, modifiedStack);
    }

    public static void increaseForgingState(ItemStack stack, int amount){
        ItemStack gotStack = getStackFromDataComponent(stack);
        gotStack.set(ModDataComponents.FORGE_STATE, gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) + amount);
        ItemStackRecord modifiedStack = new ItemStackRecord(gotStack);
        stack.set(ModDataComponents.ITEM_IN_TONGS, modifiedStack);
    }

    public static void descreaseForgingState(ItemStack stack, int amount){
        ItemStack gotStack = getStackFromDataComponent(stack);
        if (!(gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) - amount < 1)){
            gotStack.set(ModDataComponents.FORGE_STATE, gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) - amount);
        } else {
            gotStack.set(ModDataComponents.FORGE_STATE, 1);
        }
        ItemStackRecord modifiedStack = new ItemStackRecord(gotStack);
        stack.set(ModDataComponents.ITEM_IN_TONGS, modifiedStack);
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
                    ItemStack.CODEC.fieldOf("itemStack").forGetter(ItemStackRecord::itemStack)
            ).apply(instance, ItemStackRecord::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackRecord> BASIC_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ItemStackRecord::itemStack,
            ItemStackRecord::new
    );
    
}
