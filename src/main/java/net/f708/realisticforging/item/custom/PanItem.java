package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ItemStackListRecord;
import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.component.ItemsInPan;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.utils.ModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class PanItem extends Item {
    public PanItem(Properties properties) {
        super(properties);
    }

    protected static int itemAmount;

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
//        if (bothHandsBusy(player)){
//            ItemStack pan = switch (getPanHand(player)){
//                case MAIN_HAND -> player.getMainHandItem();
//                case OFF_HAND -> player.getOffhandItem();
//                case null -> null;
//            };
//            handleItemAdd(player, pan);
//        }
//    }



    protected static boolean isPanCanHoldMore(ItemStack pan){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(itemAmount), false)).record();
        return record.canHoldMore();
    }


    protected static void handleItemRemove(ItemStack pan, Player player){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(itemAmount), false)).record();
        if (!record.isEmpty()){
            ItemStack item = record.items().getLast();
            record.items().removeLast();

        }
        pan.set(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(record, false));
    }

    protected static void handleItemAdd(ItemStack input, ItemStack pan, Player player){
        ItemStackListRecord record = pan.getOrDefault(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(new ItemStackListRecord(itemAmount), false)).record();
        if (record.getUsedSlots() < record.maxSize()){
            record.addItem(input);
            input.shrink(1);
        }
        pan.set(ModDataComponents.ITEMS_IN_PAN, new ItemsInPan(record, false));
    }

    protected static InteractionHand getPanHand(Player player){
        if (player.getMainHandItem().getItem() instanceof PanItem){
            return InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof PanItem){
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

//    protected static InteractionResult handleItemAdd(Player player, ItemStack pan){
//        ItemStack offhand = switch (getPanHand(player)){
//            case MAIN_HAND -> player.getOffhandItem();
//            case OFF_HAND -> player.getOffhandItem();
//        };
//        if (offhand.is(ModTags.Items.PICKABLE_IN_PAN)){
//            if (isPanCanHoldMore(pan)){
//                ItemStackListRecord list = ItemStackListRecord.getItemsFromDataComponent(pan, itemAmount);
//                list.addItem(offhand);
//                offhand.shrink(1);
//                ItemStackListRecord.setItemsIntoDataComponent(pan, list);
//                return InteractionResult.SUCCESS_NO_ITEM_USED;
//            }
//        }
//        return InteractionResult.PASS;
//    }

    protected static boolean bothHandsBusy(Player player){
        return (!player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty());
    }

}
