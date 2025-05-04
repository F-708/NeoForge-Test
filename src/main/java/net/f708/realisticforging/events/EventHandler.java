package net.f708.realisticforging.events;

import net.f708.realisticforging.item.custom.PickedItem;
import net.f708.realisticforging.modProcedures.CleaningProcedure;
import net.f708.realisticforging.modProcedures.HotItemsProcedure;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "realisticforging")
public class EventHandler {

    @SubscribeEvent
    public static void playerRangeModified(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeMap attributeMap = player.getAttributes();
        if (player.getMainHandItem().getItem() instanceof PickedItem || player.getOffhandItem().getItem() instanceof PickedItem) {
            Utils.descreaseInteractionRange(attributeMap, player);
        } else {
            Utils.returnInteractionRange(attributeMap, player);
        }
    }


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        ProcedureHandler.PickingProcedure(event);
    }

    @SubscribeEvent
    public static void hotItemsInInventory(PlayerTickEvent.Post event) {
        HotItemsProcedure hotItemsDealDamage = new HotItemsProcedure(event);
    }

    @SubscribeEvent
    public static void coolingItem(PlayerInteractEvent.RightClickBlock event) {
        ProcedureHandler.CoolingProcedure(event.getLevel(), event.getEntity(), event.getPos());
    }

    @SubscribeEvent
    public static void coolingItem(PlayerInteractEvent.RightClickItem event) {
        ProcedureHandler.CoolingProcedure(event.getLevel(), event.getEntity(), event.getPos());
    }

    @SubscribeEvent
    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
        ProcedureHandler.ForgingProcedure(event);
    }

    @SubscribeEvent
    public static void CleanItem(PlayerInteractEvent.RightClickItem event) {
        ProcedureHandler.CleaningProcedure(event.getLevel(), event.getEntity());
    }

    @SubscribeEvent
    public static void SticksTongsGetter(PlayerInteractEvent.RightClickItem event) {
        ProcedureHandler.SticksTongsGetterProcedure(event.getLevel(), event.getEntity());
    }

    @SubscribeEvent
    public static void GrindItem(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.GrindingProcedure(event);
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
//                net.f708.realisticforging.utils.animtions.AnimationHelper.playAnimation(level, player, "ore_hit_right");
//                TickScheduler.schedule(() -> {ForgingProcess.useOnAnvil(event);}, 8);
//                event.setCanceled(true);
//
//            }
//
//        }
//
//    }
//}


