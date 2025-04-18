package net.f708.examplemod.utils.animations;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.network.packets.PacketServerPlayAnimation;
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


}