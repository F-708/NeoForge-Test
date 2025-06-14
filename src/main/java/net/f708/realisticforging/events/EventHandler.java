package net.f708.realisticforging.events;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.custom.PickingItem;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.ModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "realisticforging")
public class EventHandler {


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
//        Level level = event.getLevel();
//        BlockEntity blockEntity = level.getBlockEntity(event.getPos());
//        if (blockEntity instanceof AbstractFurnaceBlockEntity furnaceBlock){
//            Player player = event.getEntity();
//            if (TongsItem.isHoldingTongs(player)){
//                ItemStack tongs = switch (TongsItem.getHandWithTongs(player)){
//                    case MAIN_HAND -> player.getMainHandItem();
//                    case OFF_HAND -> player.getOffhandItem();
//                };
//                if (TongsItem.isTongsAreFree(tongs)){
//                }
//            }
//        }

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
    public static void useOnBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (ConditionsHelper.isHoldingHammer(player) && PickingItem.isHoldingTongs(player)) {
            if (ConditionsHelper.isMetForgingConditions(event.getLevel(), event.getEntity(), event.getPos())) {
                event.setCancellationResult(InteractionResult.PASS);
                event.setCanceled(true);
                event.setUseItem(TriState.FALSE);
                switch (ConditionsHelper.getHandWithHammer(player)){
                    case MAIN_HAND -> {
                        if (!player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())){
                            player.startUsingItem(InteractionHand.MAIN_HAND);
                        }
                    }
                    case OFF_HAND -> {
                        if (!player.getCooldowns().isOnCooldown(player.getOffhandItem().getItem())){
                            player.startUsingItem(InteractionHand.OFF_HAND);
                        }
                    }

                }
            } else if ((ConditionsHelper.isHoldingHammer(event.getEntity())) && ConditionsHelper.isForgeableBlock(event.getLevel(), event.getPos())) {
                event.setCancellationResult(InteractionResult.PASS);
                event.setCanceled(true);
            }
        }
//        if (ConditionsHelper.isHoldingForgeableItem(player, level) && PickingItem.isHoldingTongs(player) &&
//        ConditionsHelper.isForgeableBlock(level, event.getPos()) && ConditionsHelper.isOtherHandIsFree(player)) {
//            if (player.isUsingItem()){
//                return;
//            }
//            RealisticForging.LOGGER.debug("CONDITIONS ARE MET");
//            player.startUsingItem(PickingItem.getHandWithTongs(player));
//            event.setUseItem(TriState.TRUE);
//            event.setUseBlock(TriState.FALSE);
//        }
    }

    @SubscribeEvent
    public static void CleanItem(PlayerInteractEvent.RightClickItem event) {
        ProcedureHandler.CleaningProcedure(event.getLevel(), event.getEntity());
    }

//    @SubscribeEvent
//    public static void SticksTongsGetter(PlayerInteractEvent.RightClickItem event) {
//        ProcedureHandler.SticksTongsGetterProcedure(event.getLevel(), event.getEntity());
//    }

    @SubscribeEvent
    public static void GrindItem(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.GrindingProcedure(event);
    }

    @SubscribeEvent
    public static void CutItem(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.CuttingProcedure(event);
    }

    @SubscribeEvent
    public static void Carving(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.CarvingProcedure(event);
    }

    @SubscribeEvent
    public static void hotItemInInventory(PlayerTickEvent.Post event){
        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        if (inventory.contains(ModTags.Items.VERY_HOT_ITEM)) {
            player.hurt(player.damageSources().onFire(), 4f);
        }
    }

}


