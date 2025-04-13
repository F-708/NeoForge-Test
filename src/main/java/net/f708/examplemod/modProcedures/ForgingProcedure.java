package net.f708.examplemod.modProcedures;

import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.recipe.ForgingRecipe;
import net.f708.examplemod.recipe.ForgingRecipeInput;
import net.f708.examplemod.recipe.ModRecipes;
import net.f708.examplemod.utils.AnimationHelper;
import net.f708.examplemod.utils.ModTags;
import net.f708.examplemod.utils.TickScheduler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class ForgingProcedure {

    private static boolean isHoldingHammer(PlayerInteractEvent.RightClickBlock event) {
        return event.getEntity().getMainHandItem().is(ModTags.Items.HAMMER_ITEM) || (event.getEntity().getOffhandItem().is(ModTags.Items.HAMMER_ITEM));
    }

    private static boolean isCorrectBlock(PlayerInteractEvent.RightClickBlock event) {
        return event.getLevel().getBlockState(event.getPos()).is(ModTags.Blocks.FORGEABLE_BLOCK);
    }

    private static boolean isInAir(PlayerInteractEvent.RightClickBlock event) {
        return !event.getEntity().isFallFlying();
    }

    private static boolean isSleeping(PlayerInteractEvent.RightClickBlock event) {
        return !event.getEntity().isSleeping();
    }

    private static boolean isPassenger(PlayerInteractEvent.RightClickBlock event) {
        return !event.getEntity().isPassenger();
    }

    private static boolean isSprinting(PlayerInteractEvent.RightClickBlock event) {
        return !event.getEntity().isSprinting();
    }

    private static boolean isMetConditions(PlayerInteractEvent.RightClickBlock event){
        return isHoldingHammer(event)
                && isHoldingForgeableItem(event)
                && isCorrectBlock(event) && isInAir(event) && isSleeping(event) && isPassenger(event) && isSprinting(event);
    }

    private static boolean isHoldingForgeableItem(PlayerInteractEvent.RightClickBlock event) {
        boolean result = false;
        if (isHoldingHammer(event)) {

            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            if (event.getEntity().getMainHandItem().is(ModTags.Items.HAMMER_ITEM)) {
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(event.getEntity().getOffhandItem()),
                        event.getLevel());
                if (recipeOptionalOff.isPresent()) {
                    result = true;
                }
            } else {
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(event.getEntity().getMainHandItem()),
                        event.getLevel());
                if (recipeOptionalMain.isPresent()) {
                    result = true;
                }
            }
        }

        return result;
    }





    public static void accept(PlayerInteractEvent.RightClickBlock event) {
        if (isMetConditions(event)) {
            Player player = event.getEntity();
            Inventory inventory = player.getInventory();
            Level level = event.getLevel();
            int slotWithForgeable;
            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            Optional<RecipeHolder<ForgingRecipe>> recipeOptional;
            String animation = "forging_ore_right";
            ItemStack result;
            RecipeHolder<ForgingRecipe> recipeHolder;
            ItemStack Hammer;
            if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                animation = "forging_ore_left";
                slotWithForgeable = inventory.selected;
                Hammer = player.getOffhandItem();
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(event.getEntity().getMainHandItem()),
                        event.getLevel());
            } else {
                Hammer = player.getMainHandItem();
                slotWithForgeable = 40;
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(event.getEntity().getOffhandItem()),
                        event.getLevel());
            }
            if (recipeOptional.isPresent()) {
                if (player.getCooldowns().isOnCooldown(Hammer.getItem())){
                    event.setCanceled(true);
                    return;
                }
                recipeHolder = recipeOptional.get();
                int maxStage = recipeHolder.value().getMaxStage();
                result = recipeHolder.value().getResultItem(level.registryAccess());
                int currentStage = inventory.getItem(slotWithForgeable).getOrDefault(ModDataComponents.FORGE_STATE, 1);
                AnimationHelper.playAnimation(level, player, animation);
                player.getCooldowns().addCooldown(Hammer.getItem(), 45);
                event.setCanceled(true);
                TickScheduler.schedule(() -> {

                    if (currentStage >= maxStage) {
                        inventory.setItem(slotWithForgeable, result);
                    } else{
                        inventory.getItem(slotWithForgeable).set(ModDataComponents.FORGE_STATE, currentStage + 1);
                    }
                }, 45);

            }
        } else if (isHoldingHammer(event)) {
            event.setCanceled(true);
        }
    }


//    public static int maxStage;
//    public static int currentState;
//    public static String animation;
//    public static InteractionHand hand;
//    public static ItemStack result;
//    public static int slotWithInput;
//    public static ItemStack inputStack;
//    public static boolean acceptable = false;
//    public static Optional<RecipeHolder<ForgingRecipe>> recipeOptional;
//
//
//
//    public static void checkForConditions(PlayerInteractEvent.RightClickBlock event) {
//        if (event.getLevel().isClientSide) return;
//
//        Level level = event.getLevel();
//        Player player = event.getEntity();
//        Block block = level.getBlockState(event.getPos()).getBlock();
//        Inventory inventory = player.getInventory();
//        int mainHandSlot = inventory.selected;
//        int offHandSlot = 40;
//
//        if (!(block instanceof AnvilBlock)) return;
//
//
//        boolean hasHammerInMain = inventory.getItem(mainHandSlot).is(ModTags.Items.HAMMER_ITEM);
//        boolean hasHammerInOff = inventory.getItem(offHandSlot).is(ModTags.Items.HAMMER_ITEM);
//        if (hasHammerInMain){
//            animation = "forging_ore_right";
//            hand = InteractionHand.MAIN_HAND;
//        } else if (hasHammerInOff){
//            animation = "forging_ore_left";
//            hand = InteractionHand.OFF_HAND;
//        }
//
//        if (!hasHammerInMain && !hasHammerInOff) return;
//
//        slotWithInput = hasHammerInMain ? offHandSlot : mainHandSlot;
//        inputStack = inventory.getItem(slotWithInput);
//
//
//        RecipeManager recipeManager = level.getRecipeManager();
//        recipeOptional = recipeManager.getRecipeFor(
//                ModRecipes.FORGING_TYPE.get(),
//                new ForgingRecipeInput(inputStack, maxStage),
//                level
//        );
//        recipeOptional.ifPresent(recipeHolder -> {
//            if (recipeOptional.isEmpty()) {return;}
//            if (hasHammerInMain){
//                if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())){
//                    acceptable = false;
//                } else {
//                    acceptable = true;
//                    player.getCooldowns().addCooldown(inventory.getItem(mainHandSlot).getItem(), 50);
//                }
//
//
//            }
//            else {
//                if (player.getCooldowns().isOnCooldown(player.getOffhandItem().getItem())){
//                    acceptable = false;
//                } else {
//                    acceptable = true;
//                    player.getCooldowns().addCooldown(inventory.getItem(offHandSlot).getItem(), 50);
//                }
//            }
//        });
//
//        event.setCanceled(true);
//    }
//
//    public static void acceptForging(PlayerInteractEvent.RightClickBlock event) {
//        recipeOptional.ifPresent(recipe -> {
//            if (acceptable) {
//                maxStage = recipe.value().getMaxStage();
//                currentState = event.getEntity().getInventory().getItem(slotWithInput).getOrDefault(ModDataComponents.FORGE_STATE, 1);
//                if (currentState <= maxStage) {
//                    inputStack.set(ModDataComponents.FORGE_STATE, currentState + 1);
//                } else {
//                    result = recipe.value().getResultItem(event.getLevel().registryAccess());
//                    event.getEntity().getInventory().setItem(slotWithInput, result);
//                }
//                event.setCanceled(true);
//            }
//        });
//    }
//
//
//
//    public static boolean getAcceptable() {
//        return acceptable;
//    }
//
//
//    public static void playAnimation(PlayerInteractEvent.RightClickBlock event, boolean acceptable) {
//        if (acceptable) {
//            AnimationHelper.playAnimation(event.getLevel(), event.getEntity(), ForgingProcedure.getAnimation(event));
//        }
//    }
//
//
//    public static String getAnimation(PlayerInteractEvent.RightClickBlock event) {
//        boolean hasHammerInMain = event.getEntity().getInventory().getItem(event.getEntity().getInventory().selected).is(ModTags.Items.HAMMER_ITEM);
//        boolean hasHammerInOff = event.getEntity().getInventory().getItem(40).is(ModTags.Items.HAMMER_ITEM);
//        if (hasHammerInMain){
//            animation = "forging_ore_right";
//            hand = InteractionHand.MAIN_HAND;
//        } else if (hasHammerInOff){
//            animation = "forging_ore_left";
//            hand = InteractionHand.OFF_HAND;
//        }
//        return animation;
//    }


}
