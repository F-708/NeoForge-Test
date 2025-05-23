package net.f708.realisticforging.utils.animations;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static void alignPlayerAxis(Player player) {
        player.setYBodyRot(player.getYHeadRot());
    }

    public static void alightPlayerAxisToBlock(Player player, BlockPos pos) {
        // Получаем позицию игрока и цели
        double diffX = pos.getX() + 0.5 - player.getX();
        double diffZ = pos.getZ() + 0.5 - player.getZ();

        // Вычисляем целевой Yaw (в градусах) через арктангенс
        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f;

        // Коррекция угла для совместимости с системой координат Minecraft
        targetYaw = Mth.wrapDegrees(targetYaw);

        // Текущий Yaw тела игрока
        float currentYaw = player.getYRot();

        // Вычисляем разницу между целевым и текущим Yaw
        float delta = Mth.wrapDegrees(targetYaw - currentYaw);

        // Поворачиваем тело на половину разницы
        float newYaw = currentYaw + delta * 0.5f;
        player.setYBodyRot(Mth.wrapDegrees(newYaw));
    }
}
