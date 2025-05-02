package net.f708.realisticforging.utils.animations;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.network.packets.PacketServerPlayAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class AnimationHelper {

    public static void playAnimation(Player player, String animationKey) {
        PlayerAnimator.playAnimation(player.level(), player, animationKey);
        PacketDistributor.sendToServer(new PacketServerPlayAnimation(animationKey));
        PlayerHelper.alignPlayerAxis(player);
    }

    public static void cancelAnimation(Player player) {
        PlayerAnimator.cancelAnimation(player.level(), player);
    }

    public static void playForgingAnimation(InteractionHand hand) {
        Player player = Minecraft.getInstance().player;
        PlayerAnimator.cancelAnimation(Minecraft.getInstance().level, player);
        if (hand == InteractionHand.MAIN_HAND) {
            playAnimation(player, "forging_ore_right");
        }
        else {
            AnimationHelper.playAnimation(player, "forging_ore_left");
        }
    }

    public static void playPickingAnimation(InteractionHand hand) {
        Player player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "picking_item_right");
        }
        else {
            AnimationHelper.playAnimation(player, "picking_item_left");
        }
    }

    public static void playCoolingAnimation(InteractionHand hand) {
        Player player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "cooling_right");
        } else {
            AnimationHelper.playAnimation(player, "cooling_left");
        }
    }

    public static void playCleaningAnimationBareHands(InteractionHand hand) {
        Player player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "cleaning_right");
        } else {
            AnimationHelper.playAnimation(player, "cleaning_left");
        }
    }

    public static void playSticksTongsGettingAnimation(InteractionHand hand) {
        Player player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        player.swing(hand);
    }


}