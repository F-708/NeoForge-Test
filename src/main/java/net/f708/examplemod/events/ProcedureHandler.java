package net.f708.examplemod.events;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.recipe.*;
import net.f708.examplemod.utils.ModTags;
import net.f708.examplemod.utils.TickScheduler;
import net.f708.examplemod.utils.ConditionsHelper;
import net.f708.examplemod.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class ProcedureHandler {

    public static void ForgingProcedure(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (ConditionsHelper.isMetForgingConditions(level, player, event.getPos())) {

            AttributeMap attributeMap = player.getAttributes();
            Inventory inventory = player.getInventory();

            int slotWithForgeable;
            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            Optional<RecipeHolder<ForgingRecipe>> recipeOptional;
            RecipeHolder<ForgingRecipe> recipeHolder;
            ItemStack result;
            ItemStack Hammer;
            InteractionHand hand;

            if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                slotWithForgeable = inventory.selected;
                Hammer = player.getOffhandItem();
                hand = InteractionHand.OFF_HAND;
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getMainHandItem()),
                        event.getLevel());
            } else {
                Hammer = player.getMainHandItem();
                slotWithForgeable = 40;
                hand = InteractionHand.MAIN_HAND;
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getOffhandItem()),
                        event.getLevel());
            }
            if (recipeOptional.isPresent()) {
                if (player.getCooldowns().isOnCooldown(Hammer.getItem())){
                    event.setCanceled(true);
                    return;
                }
                recipeHolder = recipeOptional.get();
                int maxStage = recipeHolder.value().getMaxStage();
                result = recipeHolder.value().assemble(new ForgingRecipeInput(inventory.getItem(slotWithForgeable)), level.registryAccess());
                int currentStage = inventory.getItem(slotWithForgeable).getOrDefault(ModDataComponents.FORGE_STATE, 1);
                AnimationHandler.playForgingAnimation(hand);
                player.getCooldowns().addCooldown(Hammer.getItem(), 45);
                event.setCanceled(true);
                BlockPos pos = event.getPos();
                Utils.slowDownPlayer(attributeMap, player, 45);
                Hammer.hurtAndBreak(1, player, Hammer.getEquipmentSlot());
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 11);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 25);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 39);

                TickScheduler.schedule(() -> {
                    if (currentStage < maxStage) {
                        if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableMain = inventory.selected;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getMainHandItem()),
                                    event.getLevel());
                            if (recipeOptionalMain.isPresent()) {
                                inventory.getItem(slotWithForgeableMain).set(ModDataComponents.FORGE_STATE, currentStage + 1);
                            }
                        } else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)){
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()){
                                inventory.getItem(slotWithForgeableOff).set(ModDataComponents.FORGE_STATE, currentStage + 1);
                            }
                        }
                    } else {
                        if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableMain = inventory.selected;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getMainHandItem()),
                                    event.getLevel());
                            if (recipeOptionalMain.isPresent()){
                                inventory.setItem(slotWithForgeableMain, result);
                            }
                        }
                        else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)){
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()){
                                inventory.setItem(slotWithForgeableOff, result);
                            }
                        }
                    }
                }, 45);

            }
        } else if (ConditionsHelper.isHoldingHammer(player) && ConditionsHelper.isForgeableBlock(level, event.getPos())){
            event.setCanceled(true);
        }
    }

    public static void PickingProcedure(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (ConditionsHelper.isMetPickingConditions(level, player, event.getPos())) {
            Inventory inventory = player.getInventory();
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(event.getPos());
            int slotWithHolder;
            ItemStack Holder = null;
            InteractionHand hand = null;
            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            Optional<RecipeHolder<TongsPickingRecipe>> tongsRecipeOptional = Optional.empty();
            Optional<RecipeHolder<SticksPickingRecipe >> sticksRecipeOptional = Optional.empty();
            RecipeHolder<SticksPickingRecipe> sticksRecipeHolder;
            RecipeHolder<TongsPickingRecipe> tongsRecipeHolder;

            if (ConditionsHelper.isHoldingTongs(player)) {
                Holder = ModItems.TONGS.toStack();
            } else {
                Holder = ModItems.TWOSTICKS.toStack();
            }

            if (player.getMainHandItem().is(ModItems.TONGS)){
                slotWithHolder = inventory.selected;
                hand = InteractionHand.MAIN_HAND;
                tongsRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_PICKING_TYPE.get(),
                        new TongsPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getOffhandItem().is(ModItems.TONGS)){
                slotWithHolder = 40;
                hand = InteractionHand.OFF_HAND;
                tongsRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_PICKING_TYPE.get(),
                        new TongsPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getMainHandItem().is(ModItems.TWOSTICKS)){
                slotWithHolder = inventory.selected;
                hand = InteractionHand.MAIN_HAND;
                sticksRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.STICKS_PICKING_TYPE.get(),
                        new SticksPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getOffhandItem().is(ModItems.TWOSTICKS)){
                slotWithHolder = 40;
                hand = InteractionHand.OFF_HAND;
                sticksRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.STICKS_PICKING_TYPE.get(),
                        new SticksPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else {
                slotWithHolder = inventory.selected;
            }


            if (tongsRecipeOptional.isPresent()) {
                furnace.setItem(2, ItemStack.EMPTY);
                tongsRecipeHolder = tongsRecipeOptional.get();
                ItemStack result = tongsRecipeHolder.value().assemble(new TongsPickingRecipeInput(Holder), level.registryAccess());
                AnimationHandler.playPickingAnimation(hand);
                event.setCanceled(true);
                TickScheduler.schedule(()->{
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isHoldingTongs(player)){
                        Utils.sendPickingParticles((ServerLevel) level, event.getPos());
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TONGS)){
                            slot = inventory.selected;
                        } else {
                            slot = 40;
                        }
                        inventory.setItem(slot, result);
                    }

                }, 8);
                } else if (sticksRecipeOptional.isPresent()) {
                furnace.setItem(2, ItemStack.EMPTY);
                sticksRecipeHolder = sticksRecipeOptional.get();
                ItemStack result = sticksRecipeHolder.value().getResultItem(level.registryAccess());
                AnimationHandler.playPickingAnimation(hand);
                event.setCanceled(true);
                TickScheduler.schedule(()->{
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isHoldingSticks(player)) {
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TWOSTICKS)) {
                            slot = inventory.selected;
                        } else {
                            slot = 40;
                        }
                        inventory.setItem(slot, result);
                    }
                }, 8);
            }

        } else if (ConditionsHelper.isHoldingTongs(player) || ConditionsHelper.isHoldingSticks(player)){
            event.setCanceled(true);
        }
    }

    public static void CoolingProcedure (Level level, Player player, BlockPos pos){
        Inventory inventory = player.getInventory();
        if (ConditionsHelper.isMetCoolingConditions(player, level)) {
            if (ConditionsHelper.isMetMicsConditions(player)) {
                if (ConditionsHelper.isWaterCauldron(level.getBlockState(pos).getBlock()) || ConditionsHelper.isPlayerInWater(player)) {
                    RecipeManager recipeManager = level.getRecipeManager();

                    Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                            ModRecipes.COOLING_TYPE.get(),
                            new CoolingRecipeInput(player.getOffhandItem()),
                            level);
                    Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                            ModRecipes.COOLING_TYPE.get(),
                            new CoolingRecipeInput(player.getMainHandItem()),
                            level);

                    if (recipeOptionalOff.isPresent()) {
                        AnimationHandler.playCoolingAnimation(InteractionHand.OFF_HAND);
                        ItemStack result = recipeOptionalOff.get().value().assemble(new CoolingRecipeInput(player.getOffhandItem()), level.registryAccess());

                        TickScheduler.schedule(() -> {
                            Utils.sendCoolingParticles((ServerLevel) level, pos);
                            Utils.playCoolingSound(level, pos);
                        }, 10);
                        TickScheduler.schedule(() -> {
                            if (ConditionsHelper.isMetCoolingConditions(player, level)) {
                                inventory.setItem(40, result);

                            }
                        }, 16);

                    } else if (recipeOptionalMain.isPresent()) {
                        AnimationHandler.playCoolingAnimation(InteractionHand.MAIN_HAND);
                        ItemStack result = recipeOptionalMain.get().value().assemble(new CoolingRecipeInput(player.getMainHandItem()), level.registryAccess());

                        TickScheduler.schedule(() -> {
                            Utils.sendCoolingParticles((ServerLevel) level, pos);
                            Utils.playCoolingSound(level, pos);
                        }, 10);
                        TickScheduler.schedule(() -> {
                            if (ConditionsHelper.isMetCoolingConditions(player, level)) {
                                inventory.setItem(inventory.selected, result);
                            }

                        }, 16);
                    }
                }
            }
        }
    }

    public static void CleaningProcedure (PlayerInteractEvent.RightClickItem event){

    }


}
