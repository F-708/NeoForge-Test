package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.recipe.ForgingRecipe;
import net.f708.realisticforging.recipe.ForgingRecipeInput;
import net.f708.realisticforging.recipe.ModRecipes;
import net.f708.realisticforging.utils.*;
import net.f708.realisticforging.utils.enums.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class SmithingHammerItem extends Item {
    public SmithingHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 25;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }



//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
//            BlockHitResult hitresult = (BlockHitResult) calculateHitResult(player);
//            if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
//                    if (!Utils.checkBusy(player)) {
//                        boolean RH = false;
//                        if (ConditionsHelper.getHandWithHammer(player) == InteractionHand.MAIN_HAND) {
//                            RH = true;
//                        }
//                        if (player instanceof ServerPlayer serverPlayer) {
//                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGUPSWING, RH, 8));
//                        }
//                    }
//        }
//    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            BlockHitResult hitresult = (BlockHitResult) this.calculateHitResult(player);
            if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
                if (remainingUseDuration >= 0) {
                    if (!Utils.checkBusy(player)) {
                        boolean upswing = remainingUseDuration == 25;
                        if (upswing) {
                            boolean RH = false;
                            if (ConditionsHelper.getHandWithHammer(player) == InteractionHand.MAIN_HAND) {
                                RH = true;
                            }
                            if (player instanceof ServerPlayer serverPlayer) {
                                PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGUPSWING, RH, 10));
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (timeLeft >= 0 && livingEntity instanceof Player player) {
            if (!Utils.checkBusy(player)) {
                BlockHitResult hitresult = (BlockHitResult) this.calculateHitResult(player);
                if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
                    int i = this.getUseDuration(stack, livingEntity) - timeLeft;
                    if (i > 12 && i <= 22){

                        ItemStack tongsWithForgeable = null;
                        ItemStack hammer = null;
                        boolean RH = true;

                        switch (ConditionsHelper.getHandWithHammer(player)) {
                            case MAIN_HAND -> {hammer = player.getMainHandItem(); RH = true;}
                            case OFF_HAND -> {hammer = player.getOffhandItem(); RH = false;}
                        }

                        switch (ConditionsHelper.getHandWithForgeAble(player, level)){
                            case MAIN_HAND -> tongsWithForgeable = player.getMainHandItem();
                            case OFF_HAND -> tongsWithForgeable = player.getOffhandItem();
                        }

                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGSWING, RH, 1));
                        }

                        TickScheduler.schedule(()->{

                            ItemStack tongsWithForgeableFinal = null;
                            ItemStack hammerFinal = null;
                            boolean RHFinal = true;

                            switch (ConditionsHelper.getHandWithHammer(player)) {
                                case MAIN_HAND -> {hammerFinal = player.getMainHandItem(); RHFinal = true;}
                                case OFF_HAND -> {hammerFinal = player.getOffhandItem(); RHFinal = false;}
                            }

                            switch (ConditionsHelper.getHandWithForgeAble(player, level)){
                                case MAIN_HAND -> tongsWithForgeableFinal = player.getMainHandItem();
                                case OFF_HAND -> tongsWithForgeableFinal = player.getOffhandItem();
                            }

                            if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
                                RecipeManager recipeManager = level.getRecipeManager();


                                ItemStack inputStack = ItemStackRecord.getStack(tongsWithForgeableFinal);
                                ItemStack result;

                                int currentForgingStage = inputStack.getOrDefault(ModDataComponents.FORGE_STATE.get(), 1);
                                int finalForgingStage;

                                Optional<RecipeHolder<ForgingRecipe>> recipeOptional = recipeManager.getRecipeFor(
                                        ModRecipes.FORGING_TYPE.get(),
                                        new ForgingRecipeInput(inputStack),
                                        level);

                                if (recipeOptional.isPresent()) {
                                    result = recipeOptional.get().value().assemble(new ForgingRecipeInput(inputStack), level.registryAccess());
                                    finalForgingStage = recipeOptional.get().value().getMaxStage();
                                } else {
                                    result = null;
                                    finalForgingStage = 0;
                                }

                                if (recipeOptional.isPresent()) {
                                    int stageamount = 1;
                                    if (i == 15){
                                        Utils.sendPerfectForgingParticles((ServerLevel) level, hitresult.getBlockPos());
                                        Utils.playPerfectForgingSound(level, hitresult.getBlockPos());
                                        if (currentForgingStage != finalForgingStage - 1) {
                                            stageamount = 2;
                                        }

                                    } else {
                                        Utils.sendForgingParticles((ServerLevel) level, hitresult.getBlockPos());
                                        Utils.playForgingSound(level, hitresult.getBlockPos());
                                    }
                                    Utils.setLight(level, hitresult.getBlockPos());

                                    hammerFinal.hurtAndBreak(1, player, hammerFinal.getEquipmentSlot());
                                    player.causeFoodExhaustion(0.005f);
                                    if (level.isClientSide){
                                        CameraUtils.triggerCameraShake(4, 2, 3,4 , RHFinal);

                                    }
//                                    if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {

                                        if (currentForgingStage < finalForgingStage){
                                            RealisticForging.LOGGER.debug("INCREASING STAGE");
                                            ItemStackRecord.increaseForgingState(tongsWithForgeableFinal, stageamount);
                                            RealisticForging.LOGGER.debug(ItemStackRecord.getStack(tongsWithForgeableFinal).getOrDefault(ModDataComponents.FORGE_STATE, 1).toString());
//                                            inputStack.set(ModDataComponents.FORGE_STATE.get(), currentForgingStage + stageamount);
//                                            ItemStackRecord.setItemStackIntoDataComponent(inputStack, finalTongsWithForgeable1);
                                        } else if (!result.isEmpty()){
                                        ItemStackRecord.setItemStack(result, tongsWithForgeableFinal);
                                    }
//                                    }

                                }
                            }
                            player.stopUsingItem();
                        }, 1);
                        if (player.getData(ModData.SMITHING_HAMMER_COMBO).getCombo() < 10){
                            player.getData(ModData.SMITHING_HAMMER_COMBO).increaseCombo();
                        }
                        if (player.getData(ModData.SMITHING_HAMMER_COMBO).getCombo() > 9){
                            player.getCooldowns().addCooldown(stack.getItem(), 8);
                        } else {
                            player.getCooldowns().addCooldown(stack.getItem(), 10);
                        }
                        player.getData(ModData.SMITHING_HAMMER_COMBO).syncData(player);



                        player.stopUsingItem();

                    } else {
                        if (ConditionsHelper.isHoldingHammer(player)){
                            boolean RH = true;

                            switch (ConditionsHelper.getHandWithHammer(player)) {
                                case MAIN_HAND -> {RH = true;}
                                case OFF_HAND -> {RH = false;}
                            }

                            if (player instanceof ServerPlayer serverPlayer) {
                                PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGSWING, RH, 5));
                            }
                            TickScheduler.schedule(() -> {
                                Utils.playFailedForgingSound(level, hitresult.getBlockPos());
                            }, 5);

                        }


                        player.stopUsingItem();
                        player.getCooldowns().addCooldown(stack.getItem(), 20);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                if (ConditionsHelper.isMetForgingConditions(context.getLevel(), context.getPlayer(), BlockPos.containing(this.calculateHitResult(player).getLocation()))){
                    player.startUsingItem(context.getHand());
                }

            }
        }

        return InteractionResult.PASS;
    }

    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, p_281111_ -> !p_281111_.isSpectator() && p_281111_.isPickable(), player.blockInteractionRange()
        );
    }
}
