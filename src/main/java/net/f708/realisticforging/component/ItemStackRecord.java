package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.custom.PanItem;
import net.f708.realisticforging.item.custom.PickingItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;


public record ItemStackRecord(ItemStack itemStack) {


    public static void clearItemStack(ItemStack stack){
        Item item = stack.getItem();
        switch (item){
            case PickingItem pickingItem -> stack.remove(ModDataComponents.ITEM_IN_TONGS);
//            case PanItem panItem -> stack.remove(ModDataComponents.PAN_RESULT);
            default -> {
                RealisticForging.LOGGER.warn("Unsupported item type: {}", BuiltInRegistries.ITEM.getKey(item));
            }
        }

    }

    public static void setItemStack(ItemStack thisItem, ItemStack intoThisItem) {
        Item item = intoThisItem.getItem();
        switch (item) {
            case PickingItem pickingItem -> {
                ItemStackRecord record = new ItemStackRecord(thisItem.copy());
                intoThisItem.set(ModDataComponents.ITEM_IN_TONGS.value(), record);
            }
//            case PanItem panItem -> {
//                ItemStackRecord record = new ItemStackRecord(thisItem.copy());
//                intoThisItem.set(ModDataComponents.PAN_RESULT.value(), record);
//            }
            default -> {
                RealisticForging.LOGGER.warn("Unsupported item type: {}", BuiltInRegistries.ITEM.getKey(item));
            }
        }
    }

    public static ItemStack getStack(ItemStack stack){
        Item item = stack.getItem();
        ItemStack itemStack = ItemStack.EMPTY;
        switch (item){
            case PickingItem pickingItem -> {
                ItemStackRecord record = stack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, new ItemStackRecord(ItemStack.EMPTY));
                if (record.itemStack() instanceof ItemStack anotherStack){
                    itemStack = anotherStack.copyWithCount(1);
                }
            }
//            case PanItem panItem -> {
//                ItemStackRecord record = stack.getOrDefault(ModDataComponents.PAN_RESULT, new ItemStackRecord(ItemStack.EMPTY));
//                if (record.itemStack() instanceof ItemStack anotherStack){
//                    itemStack = anotherStack.copyWithCount(1);
//                }
//            }
            default -> {
                RealisticForging.LOGGER.warn("Unsupported item type: {}", BuiltInRegistries.ITEM.getKey(item));
            }

        }
        return itemStack;
    }

    public static void increaseForgingState(ItemStack stack, int amount){
        ItemStack gotStack = getStack(stack);
        gotStack.set(ModDataComponents.FORGE_STATE, gotStack.getOrDefault(ModDataComponents.FORGE_STATE, 1) + amount);
        setItemStack(gotStack, stack);
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




}