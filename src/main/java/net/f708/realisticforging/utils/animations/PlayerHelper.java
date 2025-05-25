package net.f708.realisticforging.utils.animations;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static void alignPlayerAxis(Player player) {
        player.setYBodyRot(player.getYHeadRot());
    }

    public static void alightPlayerAxisToBlock(Player player, BlockPos pos) {
        double diffX = pos.getX() + 0.5 - player.getX();
        double diffZ = pos.getZ() + 0.5 - player.getZ();

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f;

        targetYaw = Mth.wrapDegrees(targetYaw);

        float currentYaw = player.getYRot();

        float delta = Mth.wrapDegrees(targetYaw - currentYaw);

        float newYaw = currentYaw + delta * 0.5f;
        player.setYBodyRot(Mth.wrapDegrees(newYaw));
    }
}
