package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.TickScheduler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SledgeHammerItem extends SwordItem {
    public SledgeHammerItem(Tier tier, Properties properties) {
        super(tier, properties);
    }
    BlockHitResult traceResult;

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {


            if (!player.getCooldowns().isOnCooldown(this)) {

                boolean RH;
                if (usedHand == InteractionHand.MAIN_HAND) {
                    RH = true;
                } else {
                    RH = false;
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.SLEDGEHAMMERSWING, RH));
                }
                TickScheduler.schedule(() -> {
                    traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                            (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    Block block = level.getBlockState(traceResult.getBlockPos()).getBlock();
                    performAttack(player, player.level());
                    breakNearbyBlocks(level, traceResult.getBlockPos(), player);
                }, 14);
                player.getCooldowns().addCooldown(this, 23);

            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player){
            player.getCooldowns().addCooldown(stack.getItem(), 40);
        }
        return true;
    }

    private void performAttack(Player player, Level level) {
        float attackDamage = 15.0F; // Большой урон
        float knockback = 1.5F;

        AABB attackBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(3.0D));
        List<Entity> targets = level.getEntities(player, attackBox);

        for (Entity target : targets) {
            if (target.isAttackable()) {
                player.attack(target);
            }
        }

    }
    private void breakNearbyBlocks(Level level, BlockPos center, Player player) {
        Random random = new Random();


        if(traceResult.getDirection() == Direction.DOWN || traceResult.getDirection() == Direction.UP) {
            int randomX = random.nextBoolean() ? -1 : 1;
            int randomZ = random.nextBoolean() ? -1 : 1;

            BlockPos first = traceResult.getBlockPos().offset(randomX, 0, 0);
            BlockPos second = traceResult.getBlockPos().offset(0, 0, randomZ);



            level.destroyBlock(traceResult.getBlockPos(), true);
            level.destroyBlock(first, random.nextBoolean());
            level.destroyBlock(second, random.nextBoolean());
        }

        if(traceResult.getDirection() == Direction.NORTH || traceResult.getDirection() == Direction.SOUTH) {
            int randomX = random.nextBoolean() ? -1 : 1;
            int randomY = random.nextBoolean() ? -1 : 1;

            BlockPos first = traceResult.getBlockPos().offset(randomX, 0, 0);
            BlockPos second = traceResult.getBlockPos().offset(0, randomY, 0);

            level.destroyBlock(traceResult.getBlockPos(), true);
            level.destroyBlock(first, random.nextBoolean());
            level.destroyBlock(second, random.nextBoolean());
        }

        if(traceResult.getDirection() == Direction.EAST || traceResult.getDirection() == Direction.WEST) {
            int randomZ = random.nextBoolean() ? -1 : 1;
            int randomY = random.nextBoolean() ? -1 : 1;

            BlockPos first = traceResult.getBlockPos().offset(0, randomY, 0);
            BlockPos second = traceResult.getBlockPos().offset(0, 0, randomZ);

            level.destroyBlock(traceResult.getBlockPos(), true);
            level.destroyBlock(first, random.nextBoolean());
            level.destroyBlock(second, random.nextBoolean());
        }

//        for (int i = 0; i < random.nextInt(3, 6); i++) { // Пример: 3 случайных блока
//            BlockPos offset = center.offset(
//                    RandomSource.create().nextInt(-1, 1),
//                    RandomSource.create().nextInt(-1, 1),
//                    RandomSource.create().nextInt(-1, 1)
//            );
//            if (level.getBlockState(offset).getDestroySpeed(level, offset) >= 0) {
//                level.destroyBlock(offset, true, player);
//            }
//        }
    }
}
