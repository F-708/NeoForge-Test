package net.f708.realisticforging.utils;

import net.f708.realisticforging.attributes.ModModifiers;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.PacketServerCancelAnimation;
import net.f708.realisticforging.recipe.CleaningRecipe;
import net.f708.realisticforging.recipe.CleaningRecipeInput;
import net.f708.realisticforging.recipe.ModRecipes;
import net.f708.realisticforging.sounds.ModSounds;
import net.f708.realisticforging.utils.animations.PlayerHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Utils {

    public static void setBusy(Player player){
        player.getTags().add("BUSY");
    }
    public static void removeBusy(Player player){
        player.getTags().remove("BUSY");
    }
    public static boolean checkBusy(Player player){
        boolean busy = false;
        if (player.getTags().contains("BUSY")){
            busy = true;
        }
        return busy;
    }

    public static boolean isPlayerFarFromBlock(Player player, BlockPos pos, int amount){
        return player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > amount;
    }

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

    public static void playGrindingSound(ServerLevel level, Player player){
            level.playSound(null, player, SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 1f, 1f );

    }

    public static void sendGrindingParticles(ServerLevel level, BlockPos pos, ItemStack item){
        Random random = new Random();
        ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, item);
        level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, random.nextInt(8, 17), 0.2, 0, 0.2, 0.15);
    }

    public static void playCuttingSound(ServerLevel level, Player player){
        TickScheduler.schedule(() -> {
            level.playSound(null, player, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.PLAYERS, 1f, 1f );
            }, 15);

    }

    public static void sendCuttingParticles(ServerLevel level, BlockPos pos, ItemStack item){
        Random random = new Random();
        ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, item);
        for(int i = 0; i < 6; i++){
            TickScheduler.schedule(() -> {
                level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, random.nextInt(5, 10), 0.2, 0, 0.2, 0.15);
            }, 10 + i);
        }
    }

    public static void playCarvingSound(ServerLevel level, Player player){
        TickScheduler.schedule(() -> {
            level.playSound(null, player, ModSounds.CARVING_SOUND.get(), SoundSource.PLAYERS, 1f, 1f );
        }, 16);
        TickScheduler.schedule(() -> {
            level.playSound(null, player, ModSounds.CARVING_SOUND.get(), SoundSource.PLAYERS, 1f, 1f );
        }, 30);
        TickScheduler.schedule(() -> {
            level.playSound(null, player, ModSounds.CARVING_SOUND.get(), SoundSource.PLAYERS, 1f, 1f );
        }, 44);

    }

    public static void sendCarvingParticles(ServerLevel level, BlockPos pos, Block block){
        Random random = new Random();
        ParticleOptions particleOptions = new BlockParticleOption(ParticleTypes.BLOCK, block.defaultBlockState());
        TickScheduler.schedule(() -> {
            level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, random.nextInt(10, 20), 0.2, 0, 0.2, 0.15);
        }, 17);
        TickScheduler.schedule(() -> {
            level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, random.nextInt(10, 20), 0.2, 0, 0.2, 0.15);
        }, 30);
        TickScheduler.schedule(() -> {
            level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, random.nextInt(10, 20), 0.2, 0, 0.2, 0.15);
        }, 44);
    }

    public static void alightCarvingPlayer(Player player, BlockPos pos){
        TickScheduler.schedule(() -> {
            PlayerHelper.alightPlayerAxisToBlock(player, pos);
        }, 17);
        TickScheduler.schedule(() -> {
            PlayerHelper.alightPlayerAxisToBlock(player, pos);
        }, 30);
        TickScheduler.schedule(() -> {
            PlayerHelper.alightPlayerAxisToBlock(player, pos);
        }, 44);
    }

    public static void sendCracksToPlayers(List<Player> nearbyPlayers, Player player, BlockPos pos) {
        for (Player p : nearbyPlayers) {
            if (p instanceof ServerPlayer serverPlayer) {
                if (player instanceof ServerPlayer serverPlayerNearby) {
                    TickScheduler.schedule(() -> {
                        serverPlayerNearby.connection.send(new ClientboundBlockDestructionPacket(serverPlayerNearby.getId(), pos, 2));

                    }, 19);
                    TickScheduler.schedule(() -> {
                        serverPlayerNearby.connection.send(new ClientboundBlockDestructionPacket(serverPlayerNearby.getId(), pos, 5));

                    }, 32);
                }

            }
        }
        if (player instanceof ServerPlayer serverPlayerMain) {
            TickScheduler.schedule(() -> {
                serverPlayerMain.connection.send(new ClientboundBlockDestructionPacket(serverPlayerMain.getId(), pos, 2));

            }, 19);
            TickScheduler.schedule(() -> {
                serverPlayerMain.connection.send(new ClientboundBlockDestructionPacket(serverPlayerMain.getId(), pos, 5));

            }, 32);
        }
    }

    public static void deleteCracksToPlayers(List<Player> nearbyPlayers, Player player, BlockPos pos){
        for (Player p : nearbyPlayers) {
            if (p instanceof ServerPlayer serverPlayer) {
                if (player instanceof ServerPlayer serverPlayerNearby) {
                        serverPlayerNearby.connection.send(new ClientboundBlockDestructionPacket(serverPlayerNearby.getId(), pos, -1));
                }

            }
        }
        if (player instanceof ServerPlayer serverPlayerMain) {
                serverPlayerMain.connection.send(new ClientboundBlockDestructionPacket(serverPlayerMain.getId(), pos, -1));
        }

    }


}