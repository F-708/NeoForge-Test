package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ItemStackListRecord(List<ItemStack> items, int maxSize) {
    public ItemStackListRecord(int maxSize) {
        this(new ArrayList<>(), maxSize);
    }

    public ItemStackListRecord {
        if (items.size() > maxSize) {
            items = items.subList(0, maxSize);
        }
        items = new ArrayList<>(items);
    }

    public ItemStackListRecord addItem(ItemStack stack) {
        if (items.size() >= maxSize || stack.isEmpty()) {
            return this; // Возвращаем текущий record если нельзя добавить
        }

        List<ItemStack> newItems = new ArrayList<>(items);
        newItems.add(stack.copy()); // Копируем ItemStack для безопасности
        return new ItemStackListRecord(newItems, maxSize);
    }

    public ItemStackListRecord removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return this;
        }

        List<ItemStack> newItems = new ArrayList<>(items);
        newItems.remove(index);
        return new ItemStackListRecord(newItems, maxSize);
    }

    public ItemStackListRecord setItem(int index, ItemStack stack) {
        if (index < 0 || index >= maxSize) {
            return this;
        }

        List<ItemStack> newItems = new ArrayList<>(items);
//        while (newItems.size() <= index) {
//            newItems.add(ItemStack.EMPTY);
//        }
        newItems.set(index, stack.copy());
        return new ItemStackListRecord(newItems, maxSize);
    }

    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    public boolean isFull() {
        return items.size() >= maxSize && items.stream().noneMatch(ItemStack::isEmpty);
    }

    public boolean isEmpty() {
        return items.isEmpty() || items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean canHoldMore(){
        return items.size() < maxSize;
    }

    public int getUsedSlots() {
        return (int) items.stream().filter(stack -> !stack.isEmpty()).count();
    }

    public ItemStackListRecord clear() {
        return new ItemStackListRecord(maxSize);
    }

//    public static void setItemsIntoDataComponent(ItemStack containerItem, ItemStackListRecord record) {
//        containerItem.set(ModDataComponents.ITEM_STACK_LIST, record);
//    }

//    public static ItemStackListRecord getItemsFromDataComponent(ItemStack containerItem, int defaultMaxSize) {
//        return containerItem.getOrDefault(ModDataComponents.ITEM_STACK_LIST,
//                new ItemStackListRecord(defaultMaxSize));
//    }
//
//    public static void clearItemsFromDataComponent(ItemStack containerItem) {
//        containerItem.remove(ModDataComponents.ITEM_STACK_LIST);
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ItemStackListRecord other)) {
            return false;
        }
        return Objects.equals(this.items, other.items) && this.maxSize == other.maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, maxSize);
    }

    public static final Codec<ItemStackListRecord> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(ItemStackListRecord::items),
                    Codec.INT.fieldOf("max_size").forGetter(ItemStackListRecord::maxSize)
            ).apply(instance, ItemStackListRecord::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackListRecord> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_LIST_STREAM_CODEC, ItemStackListRecord::items,
                    ByteBufCodecs.INT, ItemStackListRecord::maxSize,
                    ItemStackListRecord::new
            );
}
