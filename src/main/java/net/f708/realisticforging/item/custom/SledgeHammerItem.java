
package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.mixin.utils.PlayerSafeAddAccessor;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.PacketPlayCameraShake;
import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.f708.realisticforging.utils.enums.Animation;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SledgeHammerItem extends DiggerItem {
    BlockHitResult traceResult;

    public SledgeHammerItem(Tier tier, Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE ,properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return player.isCreative();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 41;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration >= 0 && livingEntity instanceof Player player) {

            if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
                if (!player.getCooldowns().isOnCooldown(this)) {

                        boolean flag = remainingUseDuration % 40 == 0;
                        boolean hunger = remainingUseDuration == 20;
                        if (hunger){
                            player.causeFoodExhaustion(1f);
                        }
                        if (flag){

                            boolean RH = player.getMainHandItem().is(SledgeHammerItem.this);
                            if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                                RH = !RH;
                            }

                            if (player instanceof ServerPlayer serverPlayer) {
                                player.getData(ModData.IS_SWINGING).setSwinging(true);
                                PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
                                PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.SLEDGEHAMMERSWINGSECOND, RH, 5));
                            }
                            boolean finalRH = RH;
                            TickScheduler.schedule(() -> {
                                traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                                        (player.getEyePosition(1f).add(player.getViewVector(1f).scale(4f))),
                                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                                Block block = level.getBlockState(traceResult.getBlockPos()).getBlock();
                                performAttack(player, player.level(), finalRH, 1);
                                breakNearbyBlocks(level, traceResult.getBlockPos(), player, finalRH);
                            }, 26);

                            if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                                player.getTags().remove("SLEDGEHAMMER_COMBO");
                            } else {
                                player.getTags().add("SLEDGEHAMMER_COMBO");
                            }
                        }
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (livingEntity instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(this)){
                if (player instanceof ServerPlayer serverPlayer) {
                    int remainingTicks = this.getUseDuration(stack, livingEntity) - timeLeft;
                    applyCooldown(player, 50);
                    TickScheduler.schedule(() -> {
                        player.getData(ModData.IS_SWINGING).setSwinging(false);
                        PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
                        player.stopUsingItem();
                    }, timeLeft);
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                player.startUsingItem(usedHand);
                player.awardStat(Stats.ITEM_USED.get(this));
            } else {
                return InteractionResultHolder.consume(player.getItemInHand(usedHand));
            }
        }
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }


    private void performAttack(Player player, Level level, boolean isRight, double knockbackStrength) {
        AABB attackBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(3.0D));
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 right = direction.cross(new Vec3(0, 1, 0)).normalize();
        attackBox = attackBox.expandTowards(right.scale(1.5D));
        attackBox = attackBox.expandTowards(right.scale(-1.5D));
        List<Entity> targets = level.getEntities(player, attackBox);
        



        for (Entity target : targets) {
            if (target.isAttackable() && target != player) {
                Utils.playEntitySmashSound((ServerLevel) level, player, 1);
                player.attack(target);
                target.hurt(target.damageSources().playerAttack(player), 2);

                Vec3 horizontalDirection = !isRight ? right : right.scale(-1);

                Vec3 knockDirection = new Vec3(
                        horizontalDirection.x,
                        0.05D,
                        horizontalDirection.z
                ).normalize();

                if (level.isClientSide) continue;
                target.setDeltaMovement(knockDirection.scale(knockbackStrength));

                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.2, 0));
            }
        }
    }


    private void breakNearbyBlocks(Level level, BlockPos center, Player player, boolean RH) {
        Random random = new Random();
        ItemStack sledgehammer = player.getOffhandItem();
        List<BlockPos> blockPosList = new ArrayList<>();
        List<BlockPos> actualPosList = new ArrayList<>();
        if (player.getMainHandItem().is(ModItems.IRON_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.IRON_SLEDGEHAMMER)){
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            }
                        }
                    }
                }
                case UP, DOWN -> {
                    switch (player.getDirection()) {
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            }
                        }
                    }
                }
            }


        } else if(player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.DIAMOND_SLEDGEHAMMER)){
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST, WEST -> {
                            blockPosList.add(center);
                            if (player.getY() == center.getY()){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                            } else if (player.getY() < center.getY()){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            }

                        }
                        case SOUTH, NORTH -> {
                            blockPosList.add(center);
                            if (player.getY() == center.getY()){
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                            } else if (player.getY() < center.getY()){
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            }
                        }
                    }
                }
                case UP, DOWN -> {
                    switch (player.getDirection()) {
                        case EAST -> {
                            blockPosList.add(center);
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                        }
                        case WEST -> {
                            blockPosList.add(center);
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                        }
                    }
                }
            }
        }
        else if(player.getMainHandItem().is(ModItems.NETHERITE_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.NETHERITE_SLEDGEHAMMER)){
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST, WEST -> {
                            blockPosList.add(center);
                            if (player.getY() + 1 == center.getY()){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            } else if (player.getY() < center.getY()){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            } else if (player.getY() >= center.getY()){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                            }
                        }
                        case SOUTH, NORTH -> {
                            blockPosList.add(center);
                            if (player.getY() + 1 == center.getY()){
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            } else if (player.getY() < center.getY()){
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() - 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() - 1, center.getZ()));
                            } else if (player.getY() >= center.getY()){
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()-1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX()+1, center.getY() + 1, center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + 1, center.getZ()));
                            }
                        }
                    }
                }
                case UP, DOWN -> {
                    switch (player.getDirection()) {
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH) {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            }
                        }
                    }
                }
            }
        }
        for (BlockPos pos : blockPosList){
            if (player.getMainHandItem().is(ModItems.IRON_SLEDGEHAMMER) || player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER) || player.getMainHandItem().is(ModItems.NETHERITE_SLEDGEHAMMER)){
                sledgehammer = player.getMainHandItem();
            }
            if (ConditionsHelper.metSledgeHammerConditions((ServerPlayer) player, pos, level)){
                if (level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                    actualPosList.add(pos);
                    level.destroyBlock(pos, true, player);
                    sledgehammer.hurtAndBreak(5, player, sledgehammer.getEquipmentSlot());
                }
                if (!level.getBlockState(pos).is(Blocks.AIR) && !level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)){
                    actualPosList.add(pos);
                    sledgehammer.hurtAndBreak(1, player, sledgehammer.getEquipmentSlot());
                    level.destroyBlock(pos, random.nextBoolean(), player);
                }
            }
        }
        if (!actualPosList.isEmpty()) {
            TargetingConditions conditions = TargetingConditions.forNonCombat().range(32);
            List<Player> closePlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(8));
            List<Player> nearbyPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(16));
            List<Player> farPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(32));

            Utils.playSmashSound((ServerLevel) level, player, actualPosList.size());
            if (player instanceof ServerPlayer serverPlayer){
                PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(20 * actualPosList.size(), actualPosList.size()*1.5f, actualPosList.size() / 2, actualPosList.size() * 2, RH));
            }
            for (Player p : closePlayers){
                if (p instanceof ServerPlayer serverPlayer){
                    nearbyPlayers.remove(p);
                    farPlayers.remove(p);
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(20 * actualPosList.size(), actualPosList.size()*1.5f, actualPosList.size() / 2, actualPosList.size() * 2, random.nextBoolean()));
                }
            }
            for (Player p : nearbyPlayers){
                if (p instanceof ServerPlayer serverPlayer){
                    closePlayers.remove(p);
                    farPlayers.remove(p);
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(15 * actualPosList.size(), actualPosList.size(), actualPosList.size() / 3, actualPosList.size(), random.nextBoolean()));
                }
            }
            for (Player p : farPlayers){
                closePlayers.remove(p);
                nearbyPlayers.remove(p);
                if (p instanceof ServerPlayer serverPlayer){
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(10 * actualPosList.size(), actualPosList.size() / 2, actualPosList.size() / 4, actualPosList.size() / 2 , random.nextBoolean()));
                }
            }
        }
    }

    public static void applyCooldown(Player player, int timeAmount){
        List<Item> list = new ArrayList<>();
        list.add(ModItems.IRON_SLEDGEHAMMER.get());
        list.add(ModItems.DIAMOND_SLEDGEHAMMER.get());
        list.add(ModItems.NETHERITE_SLEDGEHAMMER.get());
        for (Item item : list){
            player.getCooldowns().addCooldown(item, timeAmount);
        }
    }

    public static boolean isHoldingSledgeHammer(Player player){
        return player.getMainHandItem().getItem() instanceof SledgeHammerItem || player.getOffhandItem().getItem() instanceof SledgeHammerItem;
    }



}

