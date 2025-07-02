package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.f708.realisticforging.utils.FluidContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record LargePlateRecord(List<ItemStack> items, FluidStack fluid) implements FluidContainer {
    public static final int CAPACITY = 1000;
    public static final int MAX_ITEMS = 4;

    public static LargePlateRecord empty() {
        return new LargePlateRecord(
                Collections.nCopies(MAX_ITEMS, ItemStack.EMPTY),
                FluidStack.EMPTY
        );
    }

    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        LargePlateRecord record = new LargePlateRecord(items, fluid);
        record.setFluid(fluid);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty) && fluid.isEmpty();
    }

    @Override
    public boolean isFull() {
        return fluid.getAmount() >= CAPACITY;
    }

    public boolean hasItems() {
        return items.stream().anyMatch(item -> !item.isEmpty());
    }

    public List<ItemStack> getNonEmptyItems() {
        return items.stream()
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    public LargePlateRecord withItem(int slot, ItemStack item) {
        if (slot < 0 || slot >= MAX_ITEMS) {
            throw new IndexOutOfBoundsException("Slot must be between 0 and " + (MAX_ITEMS - 1));
        }
        List<ItemStack> newItems = new ArrayList<>(items);
        newItems.set(slot, item.copy());
        return new LargePlateRecord(newItems, this.fluid);
    }

    public LargePlateRecord withFluid(FluidStack newFluid) {
        return new LargePlateRecord(this.items, newFluid.copy());
    }

    public LargePlateRecord withoutItem(int slot) {
        return withItem(slot, ItemStack.EMPTY);
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int findSlotWithItem(ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            if (ItemStack.isSameItem(items.get(i), stack)) {
                return i;
            }
        }
        return -1;
    }

    public static final Codec<LargePlateRecord> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.listOf().fieldOf("items").forGetter(LargePlateRecord::items),
                    FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(LargePlateRecord::fluid)
            ).apply(instance, LargePlateRecord::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, LargePlateRecord> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_LIST_STREAM_CODEC, LargePlateRecord::items,
                    FluidStack.STREAM_CODEC, LargePlateRecord::fluid,
                    LargePlateRecord::new
            );
}