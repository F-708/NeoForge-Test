package net.f708.realisticforging.utils;

import net.f708.realisticforging.attributes.ModModifiers;
import net.f708.realisticforging.recipe.CleaningRecipe;
import net.f708.realisticforging.recipe.CleaningRecipeInput;
import net.f708.realisticforging.recipe.ModRecipes;
import net.f708.realisticforging.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;


public class Utils {

    public static int getCleanableItemSlot(Level level, Player player) {
        int slot = 40;
        if (ConditionsHelper.isMetCleaningConditions(player, level)) {
            RecipeManager recipeManager = level.getRecipeManager();
            Optional<RecipeHolder<CleaningRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                    ModRecipes.CLEANING_TYPE.get(),
                    new CleaningRecipeInput(player.getMainHandItem()),
                    level);
            if (recipeOptionalMain.isPresent()) {
                slot = player.getInventory().selected;
            }
        }
        return slot;
    }

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
//        level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
        level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);
        TickScheduler.schedule(() -> {
//            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);

        }, 2);
        TickScheduler.schedule(() -> {
//            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);
            level.sendParticles(ParticleTypes.SPLASH, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(8, 16), 0, 0.1, 0, 0.55 );


        }, 4);
        TickScheduler.schedule(() -> {
//            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);

        }, 6);
        TickScheduler.schedule(() -> {
//            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);

        }, 8);
        TickScheduler.schedule(() -> {
//            level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 0.70f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0.1, 0, 0.15 );
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(0.4f, 0.6f), pos.getY() + 1.0f, pos.getZ() + random.nextFloat(0.4f, 0.7f), random.nextInt(4), 0, 0, 0, 0.05f);


        }, 10);
    }

    public static void playCoolingSound(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);

    }

    public static void sendCleaningParticles(ServerLevel level, Player player){
        Random random = new Random();
        TickScheduler.schedule(() -> {
            level.sendParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);
            level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3,5), 0.1, 0.1, 0.1, 0.15);

        }, 4);
        TickScheduler.schedule(() -> {
            level.sendParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);
            level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);

        }, 12);
        TickScheduler.schedule(() -> {
            level.sendParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);
            level.sendParticles(ParticleTypes.SMOKE , player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);

        }, 22);
        TickScheduler.schedule(() -> {
            level.sendParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);
            level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), random.nextInt(3, 5), 0.1, 0.1, 0.1, 0.15);

        }, 30);
    }

    public static void playCleaningSound(ServerLevel level, Player player){
        level.playSound(null, player, SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS, 1f, 1f );
        TickScheduler.schedule(() -> {
            level.playSound(null, player, SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS, 1f, 1f );
        }, 4);
        TickScheduler.schedule(() -> {
            level.playSound(null, player, SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS, 1f, 1f );
        }, 10);
        TickScheduler.schedule(() -> {
            level.playSound(null, player, SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS, 1f, 1f );
        }, 20);
        TickScheduler.schedule(() -> {
            level.playSound(null, player, SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS, 1f, 1f );
        }, 28);
    }
}