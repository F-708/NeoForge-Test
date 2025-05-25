package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.attributes.ModAttributes;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.SyncTagPacket;
import net.f708.realisticforging.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SledgeHammerItem extends TieredItem {
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
                    boolean RH = usedHand == InteractionHand.MAIN_HAND;
                        player.getCooldowns().addCooldown(this, 39);
                        AttributeMap attributeMap = player.getAttributes();

                Utils.slowDownPlayer(attributeMap, player, 35);
                        if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                            RH = !RH;
                        }
                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.SLEDGEHAMMERSWINGSECOND, RH));
                        }


                boolean finalRH = RH;
                TickScheduler.schedule(() -> {
                            traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                                    (player.getEyePosition(1f).add(player.getViewVector(1f).scale(4f))),
                                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                            Block block = level.getBlockState(traceResult.getBlockPos()).getBlock();
                            performAttack(player, player.level());
                            breakNearbyBlocks(level, traceResult.getBlockPos(), player, finalRH);
                        }, 26);

                        if (player.getTags().contains("SLEDGEHAMMER_COMBO")){
                            player.getTags().remove("SLEDGEHAMMER_COMBO");
                        } else {
                            player.getTags().add("SLEDGEHAMMER_COMBO");
                    }
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

        AABB attackBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(3.0D));
        List<Entity> targets = level.getEntities(player, attackBox);

        for (Entity target : targets) {
            if (target.isAttackable()) {
                player.attack(target);
            }
        }

    }
    private void breakNearbyBlocks(Level level, BlockPos center, Player player, boolean RH) {
        Random random = new Random();
        ItemStack sledgehammer = player.getOffhandItem();
        if (player.getMainHandItem().is(ModItems.IRON_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.IRON_SLEDGEHAMMER)){
            List<BlockPos> blockPosList = new ArrayList<>();
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
            for (BlockPos pos : blockPosList){
                if (player.getMainHandItem().is(ModItems.IRON_SLEDGEHAMMER)){
                    sledgehammer = player.getMainHandItem();
                }
                BlockState state = level.getBlockState(pos);
                if (ConditionsHelper.metSledgeHammerConditions((ServerPlayer) player, pos, level)){
                    if (level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                        level.destroyBlock(pos, true, player);
                    }
                    sledgehammer.hurtAndBreak(1, player, sledgehammer.getEquipmentSlot());
                    level.destroyBlock(pos, random.nextBoolean(), player);
                }
            }

        } else if(player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER) || player.getOffhandItem().is(ModItems.DIAMOND_SLEDGEHAMMER)){
            List<BlockPos> blockPosList = new ArrayList<>();
            switch (traceResult.getDirection()){
                case EAST, WEST, NORTH, SOUTH -> {
                    switch (player.getDirection()){
                        case EAST -> {
                            blockPosList.add(center);
                            if (RH){
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 2));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            } else {
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 2));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));

                            }
                        }
                        case SOUTH -> {
                            blockPosList.add(center);
                            if (RH){
                                    blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() + 2, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                            } else {
                                    blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() - 2, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            }
                        }
                        case WEST -> {
                            blockPosList.add(center);
                            if (RH){
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 2));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                            } else {
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 1));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() - 2));
                                    blockPosList.add(new BlockPos(center.getX(), center.getY(), center.getZ() + 1));
                            }
                        }
                        case NORTH -> {
                            blockPosList.add(center);
                            if (RH){
                                    blockPosList.add(new BlockPos(center.getX() - 1, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() - 2, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                            } else {
                                    blockPosList.add(new BlockPos(center.getX() + 1, center.getY(), center.getZ()));
                                    blockPosList.add(new BlockPos(center.getX() + 2, center.getY(), center.getZ()));
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
            for (BlockPos pos : blockPosList){
                if (player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER)){
                    sledgehammer = player.getMainHandItem();
                }
                BlockState state = level.getBlockState(pos);
                if (ConditionsHelper.metSledgeHammerConditions((ServerPlayer) player, pos, level)){
                    if (level.getBlockState(pos).is(Blocks.ANCIENT_DEBRIS)) {
                        level.destroyBlock(pos, true, player);
                    }
                    sledgehammer.hurtAndBreak(1, player, sledgehammer.getEquipmentSlot());
                    level.destroyBlock(pos, random.nextBoolean(), player);
                }
            }
        }

//        if(traceResult.getDirection() == Direction.DOWN || traceResult.getDirection() == Direction.UP) {
//            int randomX = random.nextBoolean() ? -1 : 1;
//            int randomZ = random.nextBoolean() ? -1 : 1;
//
//            BlockPos first = traceResult.getBlockPos().offset(randomX, 0, 0);
//            BlockPos second = traceResult.getBlockPos().offset(0, 0, randomZ);
//
//
//            level.destroyBlock(traceResult.getBlockPos(), true);
//            level.destroyBlock(first, random.nextBoolean());
//            level.destroyBlock(second, random.nextBoolean());
//        }
//
//        if(traceResult.getDirection() == Direction.NORTH || traceResult.getDirection() == Direction.SOUTH) {
//            int randomX = random.nextBoolean() ? -1 : 1;
//            int randomY = random.nextBoolean() ? -1 : 1;
//
//            BlockPos first = traceResult.getBlockPos().offset(randomX, 0, 0);
//            BlockPos second = traceResult.getBlockPos().offset(0, randomY, 0);
//
//            level.destroyBlock(traceResult.getBlockPos(), true);
//            level.destroyBlock(first, random.nextBoolean());
//            level.destroyBlock(second, random.nextBoolean());
//        }
//
//        if(traceResult.getDirection() == Direction.EAST || traceResult.getDirection() == Direction.WEST) {
//            int randomZ = random.nextBoolean() ? -1 : 1;
//            int randomY = random.nextBoolean() ? -1 : 1;
//
//            BlockPos first = traceResult.getBlockPos().offset(0, randomY, 0);
//            BlockPos second = traceResult.getBlockPos().offset(0, 0, randomZ);
//
//            level.destroyBlock(traceResult.getBlockPos(), true);
//            level.destroyBlock(first, random.nextBoolean());
//            level.destroyBlock(second, random.nextBoolean());
//        }
    }



    private List<BlockPos> breakNearbyBlocks(Level level, BlockPos center, Player player, Direction direction) {
        List<BlockPos> destroyedBlocks = new ArrayList<>();
        Random random = new Random();

        // Определите тир кувалды
        SledgehammerTier tier = SledgehammerTier.IRON; // Замените на реальную проверку тира
        if (player.getMainHandItem().is(ModItems.NETHERITE_SLEDGEHAMMER.get())) {
            tier = SledgehammerTier.NETHERITE;
        } else if (player.getMainHandItem().is(ModItems.DIAMOND_SLEDGEHAMMER.get())) {
            tier = SledgehammerTier.DIAMOND;
        }

        // Генерация случайного количества блоков
        int targetCount = tier.getRandomBlockCount(random);
        destroyedBlocks.add(center); // Центральный блок

        // Сборка соседних блоков
        List<BlockPos> candidates = new ArrayList<>();
        for (int i = 0; i < 10 && destroyedBlocks.size() < targetCount; i++) {
            BlockPos offset = getAdjacentOffset(random, direction, true);
            BlockPos nextPos = center.offset(offset.getX(), offset.getY(), offset.getZ());

            if (!destroyedBlocks.contains(nextPos) &&
                    isValidBreakBlock(level, nextPos, player)) {
                destroyedBlocks.add(nextPos);
            }
        }

        // Разрушение блоков
        for (BlockPos pos : destroyedBlocks) {
            level.destroyBlock(pos, true, player);
        }

        return destroyedBlocks;
    }

    private BlockPos getAdjacentOffset(Random random, Direction direction, boolean allowDepth) {
        // Логика выбора направления в зависимости от направления взгляда
        switch (direction) {
            case NORTH, SOUTH -> {
                int x = random.nextBoolean() ? -1 : 1;
                int y = allowDepth && random.nextBoolean() ? -1 : 0;
                return new BlockPos(x, y, 0);
            }
            case EAST, WEST -> {
                int z = random.nextBoolean() ? -1 : 1;
                int y = allowDepth && random.nextBoolean() ? -1 : 0;
                return new BlockPos(0, y, z);
            }
            case UP, DOWN -> {
                int x = random.nextBoolean() ? -1 : 1;
                int z = random.nextBoolean() ? -1 : 1;
                return new BlockPos(x, 0, z);
            }
            default -> {
                return BlockPos.ZERO;
            }
        }
    }

    private boolean isValidBreakBlock(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        return state.getDestroySpeed(level, pos) >= 0 &&
                state.is(BlockTags.MINEABLE_WITH_PICKAXE) && !state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL); // Исключите защитные блоки
    }

    private void sendDestructionPackets(Level level, List<BlockPos> destroyedBlocks, Player player) {
        TargetingConditions conditions = TargetingConditions.forNonCombat().range(32);
        List<Player> nearbyPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(32));

        for (Player p : nearbyPlayers) {
            if (p instanceof ServerPlayer serverPlayer) {
                for (BlockPos pos : destroyedBlocks) {
                    ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(
                            player.getId(), pos, 9 // Прогресс = 9 (полное разрушение)
                    );
                    serverPlayer.connection.send(packet);
                }
            }
        }
    }
}
