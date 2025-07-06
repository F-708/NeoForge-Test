package net.f708.realisticforging.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.f708.realisticforging.utils.FluidContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record   SmallPlateRecord(ItemStack item, FluidStack fluid) implements FluidContainer {
    public static final int CAPACITY = 250;

    public static SmallPlateRecord empty() {
        return new SmallPlateRecord(ItemStack.EMPTY, FluidStack.EMPTY);
    }

    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        SmallPlateRecord record = new SmallPlateRecord(item, fluid);
        record.setFluid(fluid);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty() && fluid.isEmpty();
    }

    @Override
    public boolean isFull() {
        return fluid.getAmount() >= CAPACITY;
    }

    public boolean hasItem() {
        return !item.isEmpty();
    }

    public SmallPlateRecord withItem(ItemStack newItem) {
        return new SmallPlateRecord(newItem.copy(), this.fluid);
    }

    public SmallPlateRecord withFluid(FluidStack newFluid) {
        return new SmallPlateRecord(this.item, newFluid.copy());
    }

    public SmallPlateRecord withoutItem() {
        return new SmallPlateRecord(ItemStack.EMPTY, this.fluid);
    }

    public static final Codec<SmallPlateRecord> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(SmallPlateRecord::item),
                    FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(SmallPlateRecord::fluid)
            ).apply(instance, SmallPlateRecord::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SmallPlateRecord> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC, SmallPlateRecord::item,
                    FluidStack.STREAM_CODEC, SmallPlateRecord::fluid,
                    SmallPlateRecord::new
            );
}