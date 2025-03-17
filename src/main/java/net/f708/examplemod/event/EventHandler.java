package net.f708.examplemod.event;

import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.utils.ForgingProcess;
import net.f708.examplemod.utils.TongsPickup;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Queue;

import static net.f708.examplemod.utils.ItemProcesses.FORGING_MAP;

@EventBusSubscriber(modid = "examplemod")
public class EventHandler {


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        TongsPickup tongsPickup = new TongsPickup(event);
    }

    @SubscribeEvent
    public static void hotItemsInInventory(PlayerTickEvent.Post event) {
        HotItemsDealDamage hotItemsDealDamage = new HotItemsDealDamage(event);
    }

    //    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event){
//        ForgingProcess.useOnAnvil(event);
//    }
    @SubscribeEvent
    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event) {
        ForgingProcess.useOnAnvil(event);
        Item offHand = event.getItemStack().getItem();
        Player player = event.getEntity();
        Item mainHandItem = player.getMainHandItem().getItem();
        Item offHandItem = player.getOffhandItem().getItem();

        if (FORGING_MAP.containsKey(offHand) && mainHandItem == ModItems.SMITHINGHAMMER.get()
                ||
                FORGING_MAP.containsKey(mainHandItem) && offHand == ModItems.SMITHINGHAMMER.get()
        ) {
            if (player.isShiftKeyDown()) {

            } else {
                if (offHand == ModItems.SMITHINGHAMMER.get()) {
                    player.swing(InteractionHand.OFF_HAND, true);
                    event.setCanceled(true);
                } else {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    event.setCanceled(true);
                }
            }
        }
    }
}


    











