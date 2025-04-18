package net.f708.examplemod.utils;

import net.f708.examplemod.attributes.ModModifiers;
import net.f708.examplemod.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
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

    public static void sendPickingParticles(PlayerInteractEvent.RightClickBlock event){
        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        Random random = new Random();
        level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, random.nextInt(3), 0.05d, 0.1);
    }

    public static void sendForgingParticles(PlayerInteractEvent.RightClickBlock event){
        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        Random random = new Random();
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
        level.sendParticles(ParticleTypes.LAVA, pos.getX() + random.nextFloat(0.3f, 0.7f), pos.getY() + 1.05f, pos.getZ() + random.nextFloat(0.2f, 0.8f), random.nextInt(2), 0, 0.1, 0, 0.05 );
    }

    public static void playForgingSound(PlayerInteractEvent.RightClickBlock event){
        BlockPos pos = event.getPos();
            event.getLevel().playSound(null, event.getPos(), ModSounds.FORGING_SOUND.get(), SoundSource.BLOCKS);

        }

}
