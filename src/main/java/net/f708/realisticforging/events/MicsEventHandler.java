package net.f708.realisticforging.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.data.SmithingHammerComboData;
import net.f708.realisticforging.gui.HotOverlay;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.item.custom.SledgeHammerItem;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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

    private static int tickAmount;

    @SubscribeEvent
    public static void playerRangeModified(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeMap attributeMap = player.getAttributes();
        if (ConditionsHelper.carvingRangeConditions(player)
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
            } else if (player.getData(ModData.IS_CARVING).isCarving()) {
                event.setCinematicCameraEnabled(true);
                event.setMouseSensitivity(event.getMouseSensitivity() * 1.5);
            } else {
                event.setCinematicCameraEnabled(false);
                event.setMouseSensitivity(mouseSent);
            }
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
        ResourceLocation HOTOVERLAY = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "hot/hotoverlay");
        GuiGraphics guiGraphics =event.getGuiGraphics();
        int width = event.getGuiGraphics().guiWidth();
        int height = event.getGuiGraphics().guiHeight();

        float opacity = switch (Minecraft.getInstance().player.getData(ModData.SMITHING_HAMMER_COMBO).getCombo()){
            case 1 -> 0.1f;
            case 2 -> 0.2f;
            case 3 -> 0.3f;
            case 4 -> 0.4f;
            case 5 -> 0.5f;
            case 6 -> 0.6f;
            case 7 -> 0.7f;
            case 8 -> 0.8f;
            case 9 -> 0.9f;
            case 10 -> 1.0f;
            default -> 0.0f;
        };
            try {
                Gui gui = new Gui(Minecraft.getInstance());
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
                guiGraphics.blitSprite(HOTOVERLAY, 0, 0, width, height);
            }
            finally {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
        }

    }

    @SubscribeEvent
    public static void descreaseForgingState(PlayerTickEvent.Post event){
        tickAmount++;
        if (tickAmount > 60){
            Player player = event.getEntity();
            if (player.getData(ModData.SMITHING_HAMMER_COMBO).getCombo() >= 0) {
                player.getData(ModData.SMITHING_HAMMER_COMBO).descreaseCombo();
                player.getData(ModData.SMITHING_HAMMER_COMBO).syncData(player);
                tickAmount = 0;
            }
        }
    }


}
