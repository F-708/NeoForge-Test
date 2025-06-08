package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

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


        if (!isTongsGotAnything(handWithTongs)) {
            if (!handWithItem.isEmpty()) {
                if (handWithItem.is(ModTags.Items.PICKABLE_WITH_TONGS)) {
                    ItemStackRecord.setItemStackIntoDataComponent(handWithItem, handWithTongs);
                    handWithItem.shrink(1);
                }

            } else {
                holder = InteractionResultHolder.pass(handWithTongs);
            }
        } else {
            ItemStack stackFromTongs = ItemStackRecord.getStackFromDataComponent(handWithTongs);
            ItemStackRecord.clearItemStackFromDataComponent(handWithTongs);
            if (Utils.isLeftHandFree(player) && player.isShiftKeyDown()){
                player.getInventory().setItem(40, stackFromTongs);
            } else {
                player.getInventory().add(stackFromTongs);
            }
        }


        return holder;
    }


//    @Override
//    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//        if (entity instanceof Player player) {
//            ItemStack itemStack = new ItemStack((ItemLike) Holder.direct(ModTags.Items.HOT_ITEM));
//            if (itemStack.is(ModTags.Items.PICKABLE_WITH_TONGS)) {
//                if (player.getInventory().contains(this.getDefaultInstance())) {
//                    ItemStack tongsStack = player.getInventory().getItem(this.getDefaultInstance().getEquipmentSlot().getIndex());
//                    if (tongsStack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR) != Items.AIR) {
////                        tongsStack.set(ModDataComponents.ITEM_IN_TONGS, itemStack.getItem());
//                        tongsStack.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR);
//                    }
//                }
//                if (isSelected) {
//                    if (player.getInventory().contains(ModTags.Items.HOT_ITEM)) {
//                    }
//                }
//            }
//        }
//    }

    public static boolean isTongsGotAnything(ItemStack tongs){
        return tongs.getOrDefault(ModDataComponents.ITEM_IN_TONGS, Items.AIR.getDefaultInstance()) != Items.AIR.getDefaultInstance();
    }

}
