package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ItemStackListRecord;
import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.component.ItemsInPan;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class PanItem extends Item {
    public PanItem(Properties properties) {
        super(properties);
    }

    public static int itemAmount;

    public abstract int getItemAmount();

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (stack.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(getItemAmount()), false)).melt()){
            entity.hurt(entity.damageSources().onFire(), 2);
        }
    }

        @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (bothHandsBusy(player)){
            ItemStack itemStack = switch (getPanHand(player)){
                case MAIN_HAND -> player.getOffhandItem();
                case OFF_HAND -> player.getMainHandItem();
                case null -> null;
            };
            handleItemAdd(itemStack, player.getItemInHand(usedHand), player);
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        } else {
            if (player.isShiftKeyDown()){
                handleItemRemove(player.getItemInHand(usedHand), player);
                return InteractionResultHolder.fail(player.getItemInHand(usedHand));
            }

        }
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }



    protected boolean isPanCanHoldMore(ItemStack pan){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(getItemAmount()), false)).record();
        return record.canHoldMore();
    }


    protected void handleItemRemove(ItemStack pan, Player player){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(getItemAmount()), false)).record();
        if (!record.isEmpty()){
            ItemStack item = record.items().getLast();
            record.items().removeLast();
            Utils.safeAdd(item, player);
        }
        pan.set(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(record, false));
    }

    protected void handleItemAdd(ItemStack input, ItemStack pan, Player player){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(getItemAmount()), false)).record();
        if (record.getUsedSlots() < record.maxSize()){
            record.addItem(input);
            input.shrink(1);
        }
        pan.set(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(record, false));
    }

    protected void handleItemTransferToMold(ItemStack pan, Player player){
        ItemStack mold = null;
        if (player.getMainHandItem().is(ModItems.BARSHAPEDMOLD)){
            mold = player.getMainHandItem();
        } else if (player.getOffhandItem().is(ModItems.BARSHAPEDMOLD)){
            mold = player.getOffhandItem();
        }
        if (mold == null){return;}
        ItemsInPan record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(getItemAmount()), false));
        if (record.melt()){
            
        }
    }

    protected static InteractionHand getPanHand(Player player){
        if (player.getMainHandItem().getItem() instanceof PanItem){
            return InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof PanItem){
            return InteractionHand.OFF_HAND;
        }
        return null;
    }


    protected static boolean bothHandsBusy(Player player){
        return (!player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty());
    }

}

