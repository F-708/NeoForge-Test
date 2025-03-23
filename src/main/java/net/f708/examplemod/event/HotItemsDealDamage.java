package net.f708.examplemod.event;

import net.f708.examplemod.utils.ModTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class HotItemsDealDamage {


    private static int tickCounter = 0;
    private static boolean very_hot_item(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.VERY_HOT_ITEM);
    }
    private static boolean hot_item(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.HOT_ITEM);
    }
public HotItemsDealDamage(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (tickCounter++ % 20 == 0) {
        Inventory inventory = player.getInventory();
        int total = inventory.items.stream()
                .filter(stack -> stack.is(ModTags.Items.HOT_ITEM))
                .mapToInt(ItemStack::getCount)
                .sum();
        if (inventory.contains(ModTags.Items.VERY_HOT_ITEM)) {
            player.hurt(player.damageSources().onFire(), 4f);
            tickCounter = 0;
        } else if (inventory.contains(ModTags.Items.HOT_ITEM) && total >= 3) {
            player.hurt(player.damageSources().onFire(), 2f);
            tickCounter = 0;
        }

    }
}

}
