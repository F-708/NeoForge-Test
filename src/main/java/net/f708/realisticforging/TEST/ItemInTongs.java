package net.f708.realisticforging.TEST;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record ItemInTongs(ItemStack stack) {
    public static final Codec<ItemInTongs> CODEC = ItemStack.CODEC.xmap(ItemInTongs::new, ItemInTongs::stack);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemInTongs that)) return false;
        return stack.equals(that.stack);
    }

    @Override
    public int hashCode() {
        return stack.hashCode();
    }
}