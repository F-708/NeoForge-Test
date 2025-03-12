package net.f708.examplemod.item.custom;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HotItem extends Item {
    public HotItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            if (isSelected) {
                player.hurt(player.damageSources().onFire(), 3f);
            }
            else{
                player.hurt(player.damageSources().onFire(), 1f);
            }
        }
    }
}
