package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ModDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class TongsItem extends Item {
    public TongsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {


        ItemStack handWithTongs = player.getItemInHand(usedHand);
        ItemStack handWithItem = (usedHand == InteractionHand.MAIN_HAND)
                ? player.getOffhandItem()
                : player.getMainHandItem();

        InteractionResultHolder<ItemStack> holder = InteractionResultHolder.consume(handWithTongs);


        if (handWithTongs.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR) == Items.AIR){
            if (!handWithItem.isEmpty()){
                if (!(handWithTongs.getItem() instanceof BlockItem)){
                    handWithTongs.set(ModDataComponents.ITEM_IN_TONGS, handWithItem.getItem());
                    handWithItem.shrink(1);
                }

            } else {
                holder = InteractionResultHolder.pass(handWithTongs);
            }
        } else {
            player.getInventory().add(handWithTongs.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR).getDefaultInstance());
            handWithTongs.remove(ModDataComponents.ITEM_IN_TONGS);
        }


        return holder;
    }

//    @Override
//    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//        if (entity instanceof Player player) {
//            if (isSelected) {
//                if (player.getInventory().contains(new ItemStack(ModItems.HOTRAWIRONORE.get()))) {
//                    Inventory inventory = player.getInventory();
//                    int slot = inventory.selected;
//                    inventory.setItem(slot, new ItemStack(ModItems.TONGSHOTIRON.get()));
//                    ItemStack item = inventory.getItem(slot);
//                    int ore = inventory.findSlotMatchingItem(new ItemStack(ModItems.HOTRAWIRONORE.get()));
//                    inventory.setItem(ore, new ItemStack(Items.AIR));
//                }
//            }
//        }
//    }
}
