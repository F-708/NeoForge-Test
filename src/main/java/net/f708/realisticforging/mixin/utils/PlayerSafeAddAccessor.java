package net.f708.realisticforging.mixin.utils;

import net.minecraft.world.item.ItemStack;

public interface PlayerSafeAddAccessor {
    boolean safeAdd(ItemStack stack);

    void safeAddAndDrop(ItemStack stack);
}