package net.f708.examplemod.events;

import net.f708.examplemod.utils.animations.AnimationHelper;
import net.f708.examplemod.utils.animations.PlayerAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = "examplemod", dist = Dist.CLIENT)
public class AnimationHandler {

    public static void playForgingAnimation(InteractionHand hand) {
        LocalPlayer player = Minecraft.getInstance().player;
        PlayerAnimator.cancelAnimation(Minecraft.getInstance().level, player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "forging_ore_right");
        }
        else {
            AnimationHelper.playAnimation(player, "forging_ore_left");
        }
    }

    public static void playPickingAnimation(InteractionHand hand) {
        LocalPlayer player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "picking_item_right");
        }
        else {
            AnimationHelper.playAnimation(player, "picking_item_left");
        }
    }

    public static void playCoolingAnimation(InteractionHand hand) {
        LocalPlayer player = Minecraft.getInstance().player;
        AnimationHelper.cancelAnimation(player);
        if (hand == InteractionHand.MAIN_HAND) {
            AnimationHelper.playAnimation(player, "cooling_right");
        } else {
            AnimationHelper.playAnimation(player, "cooling_left");
        }
    }

}
