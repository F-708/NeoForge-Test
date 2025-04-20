package net.f708.examplemod.utils;

import net.f708.examplemod.attributes.ModModifiers;
import net.f708.examplemod.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;


public class Utils {

    public static void slowDownPlayer(AttributeMap attributeMap, Player player, int ticks){
        attributeMap.getInstance(Attributes.MOVEMENT_SPEED).addOrUpdateTransientModifier(ModModifiers.getSlowModifier());
        TickScheduler.schedule(()->{
            attributeMap.getInstance(Attributes.MOVEMENT_SPEED).removeModifier(ModModifiers.getSlowModifier());
        }, ticks);
    }

    public static void descreaseInteractionRange(AttributeMap attributeMap, Player player){
        attributeMap.getInstance(Attributes.BLOCK_INTERACTION_RANGE).addOrUpdateTransientModifier(ModModifiers.getBlockRangeModifier());
    }
    public static void returnInteractionRange(AttributeMap attributeMap, Player player){
        attributeMap.getInstance(Attributes.BLOCK_INTERACTION_RANGE).removeModifier(ModModifiers.getBlockRangeModifier());
    }

    public static void sendPickingParticles(ServerLevel level, BlockPos pos){
        Random random = new Random();
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, random.nextInt(6), 0.1, 0.1, 0.1, 0.05);
    }

    public static void playPickingSound(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS);

    }

    public static void sendForgingParticles(ServerLevel level, BlockPos pos){
        Random random = new Random();
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
    }

    public static void playForgingSound(Level level, BlockPos pos){
            level.playSound(null, pos, ModSounds.FORGING_SOUND.get(), SoundSource.BLOCKS);

        }

        public static void sendCoolingParticles(ServerLevel level, BlockPos pos){
            Random random = new Random();
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
            level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );

            TickScheduler.schedule(() -> {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );


            }, 2);TickScheduler.schedule(() -> {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );


            }, 4);
            TickScheduler.schedule(() -> {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );


            }, 6);
            TickScheduler.schedule(() -> {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(2), 0, 0.1, 0, 0.55 );


            }, 8);
            TickScheduler.schedule(() -> {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.55 );
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.55 );


            }, 10);
        }

        public static void playCoolingSound(Level level, BlockPos pos){
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);

        }
}
