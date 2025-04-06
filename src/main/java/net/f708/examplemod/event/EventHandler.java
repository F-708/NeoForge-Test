package net.f708.examplemod.event;

import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.modProcedures.CleaningProcedure;
import net.f708.examplemod.modProcedures.ForgingProcedure;
import net.f708.examplemod.utils.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "examplemod")
public class EventHandler {


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.FURNACE)
                ||
                (event.getLevel().getBlockState(event.getPos()).is(Blocks.BLAST_FURNACE))) {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
                ItemStack MainHeldItem = event.getItemStack();
                ItemStack OffHeldItem = player.getOffhandItem();
                if (MainHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)
                        ||
                        OffHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)) {
                    AnimationHelper.playAnimation(level, player, "get_hot_ore_right");
                    TickScheduler.schedule(() -> {TongsPickup tongsPickup = new TongsPickup(event);}, 4);
                    event.setCanceled(true);
                    }
                }


        }


    }

    @SubscribeEvent
    public static void hotItemsInInventory(PlayerTickEvent.Post event) {
        HotItemsDealDamage hotItemsDealDamage = new HotItemsDealDamage(event);
    }
    @SubscribeEvent
    public static void cleaningItem(PlayerInteractEvent.RightClickItem event) {
        CleaningProcedure.cleanItem(event);
    }

    @SubscribeEvent
    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldItem = player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)
                ? player.getMainHandItem()
                : player.getOffhandItem();
            if (heldItem.is(ModTags.Items.HAMMER_ITEM)) {
                if (!event.getEntity().getCooldowns().isOnCooldown(heldItem.getItem())) {
                player.getCooldowns().addCooldown(heldItem.getItem(), 20);
                ForgingProcedure.useWithHammer(event);
                AnimationHelper.playAnimation(event.getLevel(), player, "hammer_flip");
                event.setCanceled(true);
                }
                AnimationHelper.playAnimation(level, player, "hammer_flip");
            event.setCanceled(true);
        }

    }

    //    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event){
//        ForgingProcess.useOnAnvil(event);
//    }



//    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event) {
//        Player player = event.getEntity();
//        Level level = event.getLevel();
//        Block block = level.getBlockState(event.getPos()).getBlock();
//        Item mainHandItem = player.getMainHandItem().getItem();
//        Item offHandItem = player.getOffhandItem().getItem();
//        if (block instanceof AnvilBlock) {
//            if (FORGING_MAP.containsKey(offHandItem) && mainHandItem == ModItems.SMITHINGHAMMER.get()
//                    ||
//                    FORGING_MAP.containsKey(mainHandItem) && offHandItem == ModItems.SMITHINGHAMMER.get()
//            ) {
//                if (player.getCooldowns().isOnCooldown(ModItems.SMITHINGHAMMER.get())) {
//                    event.setCanceled(true);
//                    return;
//                }
//                player.getCooldowns().addCooldown(ModItems.SMITHINGHAMMER.get(), 22);
//                player.getCooldowns().addCooldown(offHandItem, 22);
//                AnimationHelper.playAnimation(level, player, "ore_hit_right");
//                TickScheduler.schedule(() -> {ForgingProcess.useOnAnvil(event);}, 8);
//                event.setCanceled(true);
//
//            }
//
//        }
//
//    }
}


    











