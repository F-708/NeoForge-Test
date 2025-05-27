
package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.PacketPlayCameraShake;
import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SledgeHammerItemCOPY extends TieredItem {
    public SledgeHammerItemCOPY(Tier tier, Properties properties) {
        super(tier, properties);
    }
    BlockHitResult traceResult;

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000; // Бесконечное использование, пока игрок не отпустит кнопку
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        RealisticForging.LOGGER.debug("END!");
        if (livingEntity instanceof Player player && player instanceof ServerPlayer serverPlayer) {
            // Проверяем, закончился ли кулдаун
            if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                player.getData(ModData.IS_SWINGING).setSwinging(false);
                PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
            } else {
                for (int i = 1; i < 39; i++){
                    TickScheduler.schedule(() -> {
                        if (!player.getCooldowns().isOnCooldown(stack.getItem()) && player.getData(ModData.IS_SWINGING).isSwinging()) {
                            player.getData(ModData.IS_SWINGING).setSwinging(false);
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
                        }
                    }, i);
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                boolean RH = usedHand == InteractionHand.MAIN_HAND;
                player.getCooldowns().addCooldown(this, 39);

                AttributeMap attributeMap = player.getAttributes();

                Utils.slowDownPlayer(attributeMap, player, 35);


                if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                    RH = !RH;
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    boolean finalRH1 = RH;
//                            TickScheduler.schedule(() -> {
//                                PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(20, 5, finalRH1));
//                            }, 23);
                    player.getData(ModData.IS_SWINGING).setSwinging(true);
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
                    TickScheduler.schedule(()->{
//                                player.getData(ModData.IS_SWINGING).setSwinging(false);
//                                PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(player.getData(ModData.IS_SWINGING)));
                    },39);


                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.SLEDGEHAMMERSWINGSECOND, RH));
                }


                boolean finalRH = RH;
                TickScheduler.schedule(() -> {
                    traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                            (player.getEyePosition(1f).add(player.getViewVector(1f).scale(4f))),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    Block block = level.getBlockState(traceResult.getBlockPos()).getBlock();
                    performAttack(player, player.level(), finalRH, 2);
                    breakNearbyBlocks(level, traceResult.getBlockPos(), player, finalRH);
                }, 26);

                if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                    player.getTags().remove("SLEDGEHAMMER_COMBO");
                } else {
                    player.getTags().add("SLEDGEHAMMER_COMBO");
                }

            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player){
            player.getCooldowns().addCooldown(stack.getItem(), 40);
        }
        return true;
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
                Utils.playSmashSound((ServerLevel) level, player);
                player.attack(target);

                Vec3 horizontalDirection = !isRight ? right : right.scale(-1);

                // Добавляем вертикальную составляющую (вверх)
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
                case DOWN, UP, EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                    }
                }
            }


        } else if(player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.DIAMOND_SLEDGEHAMMER)){
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
//                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 2));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
//                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 2));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
//                                    blockPosList.add(new BlockPos(center.getX() + 2, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
//                                    blockPosList.add(new BlockPos(center.getX() - 2, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
//                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 2));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
//                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 2));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
//                                    blockPosList.add(new BlockPos(center.getX() - 2, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
//                                    blockPosList.add(new BlockPos(center.getX() + 2, center.getY(), center.getZ()));
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
        }
        else if(player.getMainHandItem().is(ModItems.NETHERITE_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.NETHERITE_SLEDGEHAMMER)){
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH){
                                blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                blockPosList.add(new BlockPos(center.getX(), center.getY() + (random.nextBoolean() ? 1 : -1), center.getZ()));
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
        }
        for (BlockPos pos : blockPosList){
            if (player.getMainHandItem().is(ModItems.IRON_SLEDGEHAMMER) || player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER) || player.getMainHandItem().is(ModItems.NETHERITE_SLEDGEHAMMER)){
                sledgehammer = player.getMainHandItem();
            }
            if (ConditionsHelper.metSledgeHammerConditions((ServerPlayer) player, pos, level)){
                if (level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                    level.destroyBlock(pos, true, player);
                }
                if (!level.getBlockState(pos).is(Blocks.AIR)){
                    actualPosList.add(pos);
                } else {
                    sledgehammer.hurtAndBreak(1, player, sledgehammer.getEquipmentSlot());
                    level.destroyBlock(pos, random.nextBoolean(), player);
                }
            }
        }
        if (!blockPosList.isEmpty()) {
            Utils.playSmashSound((ServerLevel) level, player);
            if (player instanceof ServerPlayer serverPlayer){
                PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayCameraShake(10 * actualPosList.size(), actualPosList.size()*2, actualPosList.size(), actualPosList.size() * 2, RH));
                RealisticForging.LOGGER.debug("Duration: " + 10 * actualPosList.size() + " intensity: " + 2 * actualPosList.size() + " waves: "+ actualPosList.size());
            }
        }
    }



}

