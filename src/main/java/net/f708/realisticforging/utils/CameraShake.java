package net.f708.realisticforging.utils;

import net.f708.realisticforging.utils.animations.PlayerHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.Random;


@OnlyIn(Dist.CLIENT)
public class CameraShake {
    private static int elapsedTicks = 0;
    private static int shakeDuration = 0;
    private static float shakeIntensity = 0.0f;
    private static int waveCount = 3;
    private static boolean RH = false;
    private static float decayRate = 0.5f;
    private static final Random random = new Random();

    public static void triggerCameraShake(int duration, float intensity, int waves, float decay, boolean isRHH) {
        shakeDuration = duration;
        RH = isRHH;
        decayRate = decay;
        shakeIntensity = intensity;
        waveCount = waves; // Задаём количество покачиваний
        elapsedTicks = 0;
    }

    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (elapsedTicks < shakeDuration) {
            // Нормализуем время в диапазон [0, 1]
            float t = (float) elapsedTicks / shakeDuration;

            // Частота колебаний (зависит от количества покачиваний)
            float frequency = waveCount * 2.0f;
            // Экспоненциальное затухание амплитуды: A(t) = A₀ * e^(-βt)
            float amplitude = (float) (shakeIntensity * Math.exp(-decayRate * t));
            // Основная синусоидальная волна
            float wave = (float) Math.sin(t * Math.PI * frequency);

            // Итоговая амплитуда с учётом затухания и случайного направления
            float finalAmplitude = wave * amplitude * (random.nextBoolean() ? 1 : -1);

            float pitchOffset;
            float yawOffset;
            float rollOffset;

            // Добавляем случайное направление для разнообразия
            if (RH) {

                pitchOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);
                yawOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);
                rollOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);

            } else {

                pitchOffset = (float) wave * amplitude * (random.nextBoolean() ? 1 : -1);
                yawOffset = (float) -wave * amplitude * (random.nextBoolean() ? 1 : -1);
                rollOffset = (float) -wave * amplitude * (random.nextBoolean() ? 1 : -1);

            }

//                event.setPitch(event.getPitch() + pitchOffset);
//                event.setYaw(event.getYaw() + yawOffset);
//                event.setRoll(event.getRoll() + rollOffset);

            event.setPitch(event.getPitch() + pitchOffset);
            event.setYaw(event.getYaw() + yawOffset);
            event.setRoll(event.getRoll() + rollOffset);

            elapsedTicks++;

        }
    }
}


//@OnlyIn(Dist.CLIENT)
//public class CameraShake {
//    private static int elapsedTicks = 0; // Счётчик прошедших тиков
//    private static int shakeDuration = 0;
//    private static float shakeIntensity = 0.0f;
//    private static boolean RH = false;
//    private static final Random random = new Random();
//
//    public static void triggerCameraShake(int duration, float intensity, boolean RHH) {
//        PlayerHelper.alignPlayerAxis(Minecraft.getInstance().player);
//        RH = RHH;
//        shakeDuration = duration;
//        shakeIntensity = intensity;
//        elapsedTicks = 0;
//    }
//
//    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
//        if (elapsedTicks < shakeDuration) {
//            float t = (float) elapsedTicks / shakeDuration;
//            float intensityFactor = 1.0f - Math.abs(2.0f * t - 1.0f);
//            float currentIntensity = shakeIntensity * intensityFactor;
//            double time = elapsedTicks * 0.1;
//            float pitchOffset;
//            float yawOffset;
//            float rollOffset;
//            if (RH) {
//
//                pitchOffset = (float) Math.sin(time * 2) * currentIntensity;
//                yawOffset = (float) Math.sin(time * 1.7) * currentIntensity;
//                rollOffset = (float) Math.sin(time * 2.3) * currentIntensity;
//
//            } else {
//
//                pitchOffset = (float) Math.sin(time * 2) * currentIntensity;
//                yawOffset = (float) -Math.sin(time * 1.7) * currentIntensity;
//                rollOffset = (float) -Math.sin(time * 2.3) * currentIntensity;
//
//            }
//            event.setPitch(event.getPitch() + pitchOffset);
//            event.setYaw(event.getYaw() + yawOffset);
//            event.setRoll(event.getRoll() + rollOffset);
//            elapsedTicks++;
//
//        } else {
//            elapsedTicks = 0;
//            shakeDuration = 0;
//            shakeIntensity = 0.0f;
//        }
//    }
//}
