package net.f708.examplemod.utils.animations;

import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static void alignPlayerAxis(Player player) {
        player.setYBodyRot(player.getYHeadRot());
    }
}
