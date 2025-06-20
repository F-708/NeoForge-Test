package net.f708.realisticforging.events;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.gui.HotOverlay;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.item.custom.SledgeHammerItem;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "realisticforging")
public class MicsEventHandler {

    @SubscribeEvent
    public static void playerRangeModified(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeMap attributeMap = player.getAttributes();
        if (ConditionsHelper.forgingRangeConditions(player)
                || ConditionsHelper.carvingRangeConditions(player)
        || (ConditionsHelper.isHoldingHammer(player) && !ConditionsHelper.isOtherHandIsFree(player))) {
            Utils.descreaseInteractionRange(attributeMap, player);
        } else {
            Utils.returnInteractionRange(attributeMap, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        player.getTags().remove("BUSY");
        player.getTags().remove("SLEDGEHAMMER_COMBO");
        player.getTags().remove("SLEDGEHAMMER_ACTIVE");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void cameraUtils(CalculatePlayerTurnEvent event){
        Player player = Minecraft.getInstance().player;
        double mouseSent = event.getMouseSensitivity();

        if (player != null){
            if (player.getData(ModData.IS_SWINGING).isSwinging()){
                event.setCinematicCameraEnabled(true);
                event.setMouseSensitivity(event.getMouseSensitivity() * 0.8);
            } else {
                event.setCinematicCameraEnabled(false);
                event.setMouseSensitivity(mouseSent);
            }
        } else {
        }

    }



    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void cancelLeftClick(InputEvent.InteractionKeyMappingTriggered event){
        Player player = Minecraft.getInstance().player;
        if (event.isAttack()){
            if (player != null){
                if (SledgeHammerItem.isHoldingSledgeHammer(player)){
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderOverlay(RenderGuiEvent.Post event){
        HotOverlay overlay = new HotOverlay(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
        event.getGuiGraphics().blitSprite(overlay.getMODEL(), 0, 0, event.getGuiGraphics().guiWidth(), event.getGuiGraphics().guiHeight());
    }


}
