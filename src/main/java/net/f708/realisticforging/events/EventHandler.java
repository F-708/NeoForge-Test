package net.f708.realisticforging.events;

import net.f708.realisticforging.item.custom.PickedItem;
import net.f708.realisticforging.item.custom.SledgeHammerItem;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.logging.Handler;

@EventBusSubscriber(modid = "realisticforging")
public class EventHandler {

    @SubscribeEvent
    public static void playerRangeModified(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeMap attributeMap = player.getAttributes();
        if (ConditionsHelper.forgingRangeConditions(player) || ConditionsHelper.carvingRangeConditions(player)) {
            Utils.descreaseInteractionRange(attributeMap, player);
        } else {
            Utils.returnInteractionRange(attributeMap, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        player.getTags().remove("BUSY");
    }



    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        ProcedureHandler.PickingProcedure(event);
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

    @SubscribeEvent
    public static void CutItem(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.CuttingProcedure(event);
    }

    @SubscribeEvent
    public static void Carving(PlayerInteractEvent.RightClickBlock event){
        ProcedureHandler.CarvingProcedure(event);
    }



}


