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

    public static void playAnimation(Player player, String animationKey, boolean RH) {
        PlayerAnimator.playAnimation(player.level(), player, animationKey, RH);
        PacketDistributor.sendToServer(new PacketServerPlayAnimation(animationKey, RH));
        PlayerHelper.alignPlayerAxis(player);
    }


    public static void cancelAnimation(Player player) {
        PlayerAnimator.cancelAnimation(player.level(), player);
    }

    public static void playSwingAnimation(Boolean RH) {
        Player player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (RH){
            player.swing(InteractionHand.MAIN_HAND);
        } else
            player.swing(InteractionHand.OFF_HAND);

    }

    public static void playForgingAnimation(Boolean RH) {
        Player player = Minecraft.getInstance().player;
            playAnimation(player, "forging_ore_right", RH);
    }


    public static void playCoolingAnimation(Boolean RH) {
        Player player = Minecraft.getInstance().player;
            AnimationHelper.playAnimation(player, "cooling_left", RH);
    }

    public static void playCleaningAnimationBareHands(Boolean RH) {
        Player player = Minecraft.getInstance().player;
            AnimationHelper.playAnimation(player, "cleaning_right", RH);
    }

    public static void playSticksTongsGettingAnimation(Boolean RH) {
        Player player = Minecraft.getInstance().player;
        if (RH){
            player.swing(InteractionHand.MAIN_HAND);
        }
        else player.swing(InteractionHand.OFF_HAND);

    }

    public static void playStartChiselingAnimation(Boolean RH){
        Player player = Minecraft.getInstance().player;
            AnimationHelper.playAnimation(player, "start_chiseling_action_right", RH);
    }


    public static void playCarvingAnimation(Boolean RH){
        Player player = Minecraft.getInstance().player;
        AnimationHelper.playAnimation(player, "carving_ore_right", RH);
    }

    public static void playCuttingAnimation(Boolean RH){
        Player player = Minecraft.getInstance().player;
        AnimationHelper.playAnimation(player, "cutting_animation_long", RH);
    }

    public static void playSledgeHammerAnimation(Boolean RH){
        Player player = Minecraft.getInstance().player;
        AnimationHelper.playAnimation(player, "sledgehammer_swing_first35", RH);
    }

    public static void playSledgeHammerAnimationCombo(Boolean RH){
        Player player = Minecraft.getInstance().player;
        AnimationHelper.playAnimation(player, "sledgehammer_swing_first12", RH);
    }





}