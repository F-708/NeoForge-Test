package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.recipe.ForgingRecipe;
import net.f708.realisticforging.recipe.ForgingRecipeInput;
import net.f708.realisticforging.recipe.ModRecipes;
import net.f708.realisticforging.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
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


    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration >= 0 && livingEntity instanceof Player player) {
            RealisticForging.LOGGER.debug("PASSED REMAINING DURATION: " + remainingUseDuration);
            if (!Utils.checkBusy(player)) {
                RealisticForging.LOGGER.debug("NOT BUSY");
                BlockHitResult hitresult = (BlockHitResult) this.calculateHitResult(player);
                if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
                    RealisticForging.LOGGER.debug("CONDITIONS ARE MET");
                    boolean upswing = remainingUseDuration == 25;
                    if (upswing) {
                        boolean RH = ConditionsHelper.isHammerInRightHand(player);
                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGUPSWING, RH, 8));
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


                        boolean RH = ConditionsHelper.isHammerInRightHand(player);

                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGSWING, RH, 2));
                        }
                        TickScheduler.schedule(()->{

                            RecipeManager recipeManager = level.getRecipeManager();

                            boolean forgeableItemInRH = ConditionsHelper.isForgeableItemInRH(player, level);

                            ItemStack itemToUse = forgeableItemInRH
                                    ? player.getMainHandItem()
                                    : player.getOffhandItem();

                            ItemStack inputStack = forgeableItemInRH ? player.getMainHandItem() : player.getOffhandItem();

                            ItemStack result;
                            int currentForgingStage = inputStack.getOrDefault(ModDataComponents.FORGE_STATE.get(), 1);
                            int finalForgingStage;

                            Optional<RecipeHolder<ForgingRecipe>> recipeOptional = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(itemToUse),
                                    level);
                            if (recipeOptional.isPresent()) {
                                result = recipeOptional.get().value().assemble(new ForgingRecipeInput(itemToUse), level.registryAccess());
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
                                    stageamount = 2;
                                } else {
                                    Utils.sendForgingParticles((ServerLevel) level, hitresult.getBlockPos());
                                    Utils.playForgingSound(level, hitresult.getBlockPos());
                                }

                                Utils.setLight(level, hitresult.getBlockPos(), 2);

                                if (forgeableItemInRH) {
                                    player.getOffhandItem().hurtAndBreak(1, player, player.getOffhandItem().getEquipmentSlot());
                                } else {
                                    player.getMainHandItem().hurtAndBreak(1, player, player.getMainHandItem().getEquipmentSlot());
                                }

                                CameraUtils.triggerCameraShake(4, 2, 2,4 ,ConditionsHelper.isHammerInRightHand(player));
                                if (ConditionsHelper.isMetForgingConditions(level, player, hitresult.getBlockPos())) {
                                    if (currentForgingStage < finalForgingStage){
                                        inputStack.set(ModDataComponents.FORGE_STATE.get(), currentForgingStage + stageamount);
                                    }  else {
                                        int slot = 40;
                                        if (forgeableItemInRH) {
                                            slot = player.getInventory().selected;
                                        }
                                        player.getInventory().setItem(slot, result);
                                    }
                                }

                            }

                            player.stopUsingItem();
                        }, 1);


                        player.getCooldowns().addCooldown(stack.getItem(), 10);

                        player.stopUsingItem();

                    } else {
                        player.stopUsingItem();
                        player.getCooldowns().addCooldown(stack.getItem(), 40);
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
                player.startUsingItem(context.getHand());
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
