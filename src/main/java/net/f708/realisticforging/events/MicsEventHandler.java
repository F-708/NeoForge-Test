package net.f708.realisticforging.events;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "realisticforging")
public class MicsEventHandler {

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
        player.getTags().remove("SLEDGEHAMMER_COMBO");
        player.getTags().remove("SLEDGEHAMMER_ACTIVE");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void cameraUtils(CalculatePlayerTurnEvent event){
        RealisticForging.LOGGER.debug("EVENT OF CAMERRA TRIGGERED");
        Player player = Minecraft.getInstance().player;
        double mouseSent = event.getMouseSensitivity();

        if (player != null){
            if (player.getTags().contains("SLEDGEHAMMER_ACTIVE")){
                TickScheduler.schedule(() -> {
                    player.getTags().remove("SLEDGEHAMMER_ACTIVE");
                    RealisticForging.LOGGER.debug("REMOVED!");

                }, 39);
                RealisticForging.LOGGER.debug("SET CAMERA!");
                event.setCinematicCameraEnabled(true);
                event.setMouseSensitivity(event.getMouseSensitivity() / 2);
            } else {
                event.setCinematicCameraEnabled(false);
                event.setMouseSensitivity(mouseSent);
            }
        } else {
            RealisticForging.LOGGER.debug("PLAYER IS NULL");
        }

    }

}
