package net.f708.examplemod.events;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.recipe.*;
import net.f708.examplemod.utils.ModTags;
import net.f708.examplemod.utils.TickScheduler;
import net.f708.examplemod.utils.ConditionsHelper;
import net.f708.examplemod.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class ProcedureHandler {

    public static void ForgingProcedure(PlayerInteractEvent.RightClickBlock event) {
        if (ConditionsHelper.isMetForgingConditions(event)) {
            Player player = event.getEntity();
            AttributeMap attributeMap = player.getAttributes();
            Inventory inventory = player.getInventory();
            Level level = event.getLevel();
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
                Utils.slowDownPlayer(attributeMap, player, 45);
                Hammer.hurtAndBreak(1, player, Hammer.getEquipmentSlot());
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(event);
                    Utils.sendForgingParticles(event);
                }, 11);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(event);
                    Utils.sendForgingParticles(event);
                }, 25);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(event);
                    Utils.sendForgingParticles(event);
                }, 39);
                if (currentStage >= maxStage) {
                    inventory.setItem(slotWithForgeable, result);
                }
                TickScheduler.schedule(() -> {
                    if (currentStage < maxStage) {
                        inventory.getItem(slotWithForgeable).set(ModDataComponents.FORGE_STATE, currentStage + 1);
                    }
                }, 45);

            }
        } else if (ConditionsHelper.isHoldingHammer(event) && ConditionsHelper.isForgeableBlock(event)){
            event.setCanceled(true);
        }
    }

    public static void PickingProcedure(PlayerInteractEvent.RightClickBlock event){
        if (ConditionsHelper.isMetPickingConditions(event)) {
            Player player = event.getEntity();
            Level level = event.getLevel();
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

            if (ConditionsHelper.isHoldingTongs(event)) {
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
                TickScheduler.schedule(() -> {
                        inventory.setItem(slotWithHolder, result);
                }, 8);
                } else if (sticksRecipeOptional.isPresent()) {
                furnace.setItem(2, ItemStack.EMPTY);
                sticksRecipeHolder = sticksRecipeOptional.get();
                ItemStack result = sticksRecipeHolder.value().getResultItem(level.registryAccess());
                AnimationHandler.playPickingAnimation(hand);
                event.setCanceled(true);
                TickScheduler.schedule(() -> {
                    inventory.setItem(slotWithHolder, result);
                }, 8);
            }

        } else if (ConditionsHelper.isHoldingTongs(event) || ConditionsHelper.isHoldingSticks(event)){
            event.setCanceled(true);
        }
    }

    public static void CoolingProcedure (PlayerInteractEvent.RightClickBlock event){
        ExampleMod.LOGGER.debug("cooling procedure started");
        if (ConditionsHelper.isMetCoolingConditions(event)) {
            ExampleMod.LOGGER.debug("cooling procedure conditions met");

            Player player = event.getEntity();
            Level level = event.getLevel();
            Inventory inventory = player.getInventory();

            RecipeManager recipeManager = event.getLevel().getRecipeManager();

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
                TickScheduler.schedule(()->
                        inventory.setItem(40, result), 16);

            } else if (recipeOptionalMain.isPresent()) {
                AnimationHandler.playCoolingAnimation(InteractionHand.MAIN_HAND);
                ItemStack result = recipeOptionalMain.get().value().assemble(new CoolingRecipeInput(player.getMainHandItem()), level.registryAccess());
                TickScheduler.schedule(()->
                        inventory.setItem(inventory.selected, result), 16);
            }
        }
    }

}
