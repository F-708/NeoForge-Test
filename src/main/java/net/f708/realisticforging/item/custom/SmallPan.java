package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ModDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmallPan extends Item {
    public SmallPan(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return super.use(level, player, usedHand);
    }

//    public static void handlePickup(Level level, Player player, InteractionHand hand) {
//        ItemStack pan = null;
//        ItemStack item = null;
//        switch(handWithPan(player)) {
//            case MAIN_HAND -> {
//                pan = player.getMainHandItem();
//                item = player.getOffhandItem();
//            }
//            case OFF_HAND -> {
//                pan = player.getOffhandItem();
//                item = player.getMainHandItem();
//            }
//        }
////        if (pan.getOrDefault(ModDataComponents.SMALL_PLATE_DATA, ))
//
//    }

//    public static InteractionHand handWithPan(Player player) {
//        if (player.getMainHandItem().is(this)){
//            return InteractionHand.MAIN_HAND;
//        }
//        return InteractionHand.OFF_HAND;
//    }
//
//    public static  InteractionHand handWithItem(Player player) {
//        if (player.getMainHandItem().is(this)){
//            return InteractionHand.OFF_HAND;
//        }
//        return InteractionHand.MAIN_HAND;
//    }
}
