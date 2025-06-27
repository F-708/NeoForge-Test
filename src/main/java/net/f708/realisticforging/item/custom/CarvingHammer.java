package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.f708.realisticforging.recipe.CarvingRecipe;
import net.f708.realisticforging.recipe.CarvingRecipeInput;
import net.f708.realisticforging.recipe.ModRecipes;
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class CarvingHammer extends Item {
    public CarvingHammer(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (isHoldingChiselAndCarvingHammer(player)) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                player.startUsingItem(usedHand);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.pass(player.getItemInHand(usedHand));
            }
        }
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }



    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        Player player = (Player) livingEntity;
        if (player.getCooldowns().isOnCooldown(this)) {return;}
        if (remainingUseDuration == 20) {
            if (!player.getData(ModData.IS_CARVING).isCarving()){
                player.getData(ModData.IS_CARVING).setCarving(true);
                player.getData(ModData.IS_CARVING).syncData(player);
                boolean RH = player.getMainHandItem().is(CarvingHammer.this);
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.CARVINGSTART, RH, 1));
                }
                return;
            } else {
                boolean RH = player.getMainHandItem().is(CarvingHammer.this);
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.CARVINGHIT, RH, 4));
                }
            }
            if (player.getData(ModData.IS_CARVING).isCarving()){
                TickScheduler.schedule(()-> {
                    BlockHitResult traceResult = getBlockHitResult(level, player);
                    RecipeManager recipeManager = level.getRecipeManager();
                    Block block = level.getBlockState(traceResult.getBlockPos()).getBlock();
                    BlockPos pos = traceResult.getBlockPos();
                    Random random = new Random();

                    BlockPos storedPos = stack.getOrDefault(ModDataComponents.BLOCKPOS, pos);
                    Direction storedDirection = stack.get(ModDataComponents.BLOCK_DIRECTION);
                    Direction currentDirection = traceResult.getDirection();

                    Optional<RecipeHolder<CarvingRecipe>> recipeBlock = recipeManager.getRecipeFor(
                            ModRecipes.CARVING_TYPE.get(), (new CarvingRecipeInput(block.asItem().getDefaultInstance())), level);

                    Block resultBlock = Blocks.AIR;
                    ItemStack resultItem = ItemStack.EMPTY;
                    int currentStage = stack.getOrDefault(ModDataComponents.CARVING_STATE, 1);
                    int finalStage = 0;






                    if (recipeBlock.isPresent()) {
                        if (recipeBlock.get().value().getOutPutBlock().getItem() instanceof BlockItem blockItem){
                            resultBlock = blockItem.getBlock();
                        }
                        finalStage = recipeBlock.get().value().getHitAmount();
                        resultItem = recipeBlock.get().value().getResultItem();

                        if (currentStage < finalStage){

                            checkBlockHit(player, level, stack, pos);
                            int chance = random.nextInt(4);
                            if (chance < 3) {
                                basicBlockHit(player, level, stack, pos);
                            } else {
                                doCheckBlockHit(player, level, stack, pos);
                            }


//                            stack.set(ModDataComponents.CARVING_STATE, stack.getOrDefault(ModDataComponents.CARVING_STATE, 1) + 1);
//                            RealisticForging.LOGGER.debug("CARVING STATE CHANGED: " + stack.getOrDefault(ModDataComponents.CARVING_STATE, 1));

                        } else {
                            stack.set(ModDataComponents.CARVING_STATE, 1);
                            level.destroyBlock(pos, false);
                            level.setBlockAndUpdate(pos, resultBlock.defaultBlockState());
                            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, resultItem));
                            releaseUsing(stack, level, livingEntity, remainingUseDuration - this.getUseDuration(stack, livingEntity));
                        }

                    }
                    performAttack(player, level);
                }, 6);
            }
        }
    }

    private void performAttack(Player player, Level level) {
        AABB attackBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(2.0D));
        List<Entity> targets = level.getEntities(player, attackBox);
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 right = direction.cross(new Vec3(0, 1, 0)).normalize();
        attackBox = attackBox.expandTowards(right.scale(0.2D));
        attackBox = attackBox.expandTowards(right.scale(-0.2D));
        for (Entity target : targets) {
            if (target.isAttackable() && target != player) {
                player.attack(target);
            }
        }
    }

    public static void basicBlockHit(Player player, Level level, ItemStack hammer, BlockPos pos){
        increaseCarving(hammer);
        Utils.sendCarvingParticles((ServerLevel) level, pos, level.getBlockState(pos).getBlock());
        Utils.playCarvingSound((ServerLevel) level, player);
    }

    public static void doCheckBlockHit(Player player, Level level, ItemStack hammer, BlockPos pos){
        increaseCarving(hammer);
        Direction currentDirection = getBlockHitResult(level, player).getDirection();
            hammer.set(ModDataComponents.BLOCK_DIRECTION, currentDirection);
            Utils.sendWarningCarvingParticles((ServerLevel) level, pos, level.getBlockState(pos).getBlock());
            Utils.playWarningCarvingSound((ServerLevel) level, player);
    }

    public static void checkBlockHit(Player player, Level level, ItemStack hammer, BlockPos pos){
        Direction storedDirection = hammer.get(ModDataComponents.BLOCK_DIRECTION);
        Direction currentDirection = getBlockHitResult(level, player).getDirection();

        if (storedDirection != null && storedDirection == currentDirection) {
            int breakAmount = hammer.getOrDefault(ModDataComponents.BREAK_AMOUNT, 0) + 1;
            hammer.set(ModDataComponents.BREAK_AMOUNT, breakAmount);

            if (breakAmount >= 2) {
                level.destroyBlock(pos, false);
                hammer.set(ModDataComponents.BREAK_AMOUNT, 0);
            } else {
                TargetingConditions conditions = TargetingConditions.forNonCombat().range(32);
                List<Player> nearbyPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(32));
                Utils.sendCracksToPlayers(nearbyPlayers, player, pos);
                level.playSound(null, player, SoundEvents.DEEPSLATE_BREAK, SoundSource.PLAYERS, 1f, 1.3f);
            }
        }
        hammer.set(ModDataComponents.BLOCK_DIRECTION, null);
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player){
            RealisticForging.LOGGER.debug("releaseUsing called");
            player.getData(ModData.IS_CARVING).setCarving(false);
            player.getData(ModData.IS_CARVING).syncData(player);
            player.stopUsingItem();
            stack.set(ModDataComponents.CARVING_STATE, 1);
            stack.set(ModDataComponents.BLOCKPOS, BlockPos.ZERO);
            stack.set(ModDataComponents.BLOCK_DIRECTION, null);
            stack.set(ModDataComponents.BREAK_AMOUNT, 0);
            TargetingConditions conditions = TargetingConditions.forNonCombat().range(32);
            List<Player> nearbyPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(32));
            Utils.deleteCracksToPlayers(nearbyPlayers, player, getBlockHitResult(level, player).getBlockPos());
        }
    }

    public static InteractionHand getHandWithCarvingHammer(Player player) {
        if (player.getMainHandItem().getItem() instanceof CarvingHammer) {
            return InteractionHand.MAIN_HAND;
        } else if(player.getOffhandItem().getItem() instanceof CarvingHammer) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static InteractionHand getHandWithChisel(Player player) {
        if (player.getMainHandItem().is(ModItems.POINTCHISEL)) {
            return InteractionHand.MAIN_HAND;
        } else if(player.getOffhandItem().is(ModItems.POINTCHISEL)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static void increaseCarving(ItemStack hammer){
        hammer.set(ModDataComponents.CARVING_STATE, hammer.getOrDefault(ModDataComponents.CARVING_STATE, 1) + 1);
    }

    public static boolean isHoldingChiselAndCarvingHammer(Player player) {
        return (player.getMainHandItem().is(ModItems.POINTCHISEL) || player.getOffhandItem().is(ModItems.POINTCHISEL))
                &&
                (player.getMainHandItem().is(ModItems.CARVINGHAMMER) || player.getOffhandItem().is(ModItems.CARVINGHAMMER));
    }

    public static BlockHitResult getBlockHitResult(Level level, Player player) {
        return level.clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(2f))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    //                    if (!storedPos.equals(pos)) {
//                        stack.set(ModDataComponents.CARVING_STATE, 1);
//                        stack.set(ModDataComponents.BLOCK_DIRECTION, null);
//                        stack.set(ModDataComponents.BREAK_AMOUNT, 0);
//                    }
//                    stack.set(ModDataComponents.BLOCKPOS, pos);
//
//                    if (random.nextInt(4) == 0) {
//                        Utils.playWarningCarvingSound((ServerLevel) level, player);
//                        Utils.sendWarningCarvingParticles((ServerLevel) level, pos, level.getBlockState(pos).getBlock());
//
//                        stack.set(ModDataComponents.BLOCK_DIRECTION, currentDirection);
//                        stack.set(ModDataComponents.BREAK_AMOUNT, 0);
//                        return;
//                    }
//
//                    if (storedDirection != null) {
//                        Utils.playCarvingSound((ServerLevel) level, player);
//                        Utils.sendCarvingParticles((ServerLevel) level, pos, block);
//                        if (storedDirection == currentDirection) {
//                            int breakAmount = stack.getOrDefault(ModDataComponents.BREAK_AMOUNT, 0);
//
//                            if (breakAmount == 1 && random.nextBoolean()) {
//                                level.destroyBlock(pos, false);
//                                stack.set(ModDataComponents.BLOCK_DIRECTION, null);
//                                stack.set(ModDataComponents.BREAK_AMOUNT, 0);
//                                TargetingConditions conditions = TargetingConditions.forNonCombat().range(32);
//
//                                List<Player> nearbyPlayers = level.getNearbyPlayers(conditions, player, player.getBoundingBox().inflate(32));
//
//                                Utils.sendCracksToPlayers(nearbyPlayers, player, pos);
//                                return;
//                            }
//
//                            if (breakAmount == 2) {
//                                level.destroyBlock(pos, false);
//                                stack.set(ModDataComponents.BLOCK_DIRECTION, null);
//                                stack.set(ModDataComponents.BREAK_AMOUNT, 0);
//                                return;
//                            }
//
//                            stack.set(ModDataComponents.BREAK_AMOUNT, breakAmount + 1);
//                        }
//                        else {
//                            stack.set(ModDataComponents.BLOCK_DIRECTION, currentDirection);
//                            stack.set(ModDataComponents.BREAK_AMOUNT, 0);
//                        }
//                    }

//                    if (stack.get(ModDataComponents.BLOCK_DIRECTION) != null) {
//                        if (Objects.equals(stack.get(ModDataComponents.BLOCK_DIRECTION), traceResult.getDirection())){
//                            stack.set(ModDataComponents.BREAK_AMOUNT, stack.getOrDefault(ModDataComponents.BREAK_AMOUNT, 1) + 1);

//                            if (stack.getOrDefault(ModDataComponents.BREAK_AMOUNT, 1) == 3){
//                                block.destroy(level, pos, level.getBlockState(pos));
//                                stack.set(ModDataComponents.BLOCK_DIRECTION, null);
//                                return;
//                            }
//                            stack.set(ModDataComponents.BLOCK_DIRECTION, null);
//                        }
//                    }
//
//                    if (random.nextBoolean()){
//                        Utils.playWarningCarvingSound((ServerLevel) level, player);
//                        Utils.sendWarningCarvingParticles((ServerLevel) level, pos, level.getBlockState(pos).getBlock());
//                        stack.set(ModDataComponents.BLOCK_DIRECTION, traceResult.getDirection());
//                    } else {
//                        Utils.playCarvingSound((ServerLevel) level, player);
//                        Utils.sendCarvingParticles((ServerLevel) level, pos, block);
//                    }



}
