package net.f708.realisticforging.utils;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.item.custom.SmithingHammerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

import javax.swing.text.View;
import java.util.Random;

@EventBusSubscriber(modid = RealisticForging.MODID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class CameraUtils {
    private static int elapsedTicks = 0;
    private static int shakeDuration = 0;
    private static float shakeIntensity = 0.0f;
    private static int waveCount = 3;
    private static boolean RH = false;
    private static float decayRate = 0.5f;
    private static final Random random = new Random();



    public static void triggerCameraShake(int duration, float intensity, int waves, float decay, boolean isRHH) {
        RH = isRHH;
        decayRate = decay;
        shakeIntensity = intensity;
        waveCount = waves;
        int fps = Minecraft.getInstance().getFps();
        float scalingFactor = (float) fps / 60.0f;
        shakeDuration = (int) (duration * scalingFactor);
        elapsedTicks = 0;
    }


    @SubscribeEvent
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
                if (elapsedTicks < shakeDuration) {
                    float t = (float) elapsedTicks / shakeDuration;

                    float frequency = waveCount * 2.0f;
                    float amplitude = (float) (shakeIntensity * Math.exp(-decayRate * t));
                    float wave = (float) Math.sin(t * Math.PI * frequency);

                    float finalAmplitude = wave * amplitude * (random.nextBoolean() ? 1 : -1);

                    float pitchOffset;
                    float yawOffset;
                    float rollOffset;

                    if (RH) {

                        pitchOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);
                        yawOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);
                        rollOffset = wave * amplitude * (random.nextBoolean() ? 1 : -1);

                    } else {

                        pitchOffset = (float) wave * amplitude * (random.nextBoolean() ? 1 : -1);
                        yawOffset = (float) -wave * amplitude * (random.nextBoolean() ? 1 : -1);
                        rollOffset = (float) -wave * amplitude * (random.nextBoolean() ? 1 : -1);

                    }

                    event.setPitch(event.getPitch() + pitchOffset);
                    event.setYaw(event.getYaw() + yawOffset);
                    event.setRoll(event.getRoll() + rollOffset);

                    elapsedTicks++;
        }
    }



}
