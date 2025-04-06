package net.f708.examplemod.modProcedures;

import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.recipe.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.Optional;

public class CleaningProcedure {
    private static Player player;
    private static Level level;;
    private static ItemStack mainHandStack;
    private static ItemStack offHandStack;

    public static void bareHands(PlayerInteractEvent.RightClickItem event) {
        level = event.getLevel();
        player = event.getEntity();
    ItemStack mainHandStack = player.getMainHandItem();
    ItemStack offHandStack = player.getOffhandItem();
    RecipeManager recipeManager = level.getRecipeManager();
    if (cleaningAbleItemInHand(mainHandStack, recipeManager)) {
        
        }
    }


    public static void cleanItem(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        Block block = level.getBlockState(event.getPos()).getBlock();
        BlockState state = level.getBlockState(event.getPos());
        Inventory inventory = player.getInventory();
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();
        int mainHandSlot = inventory.selected;
        int offHandSlot = 40;
        RecipeManager recipeManager = level.getRecipeManager();
        boolean itemInMainHand = mainHandStack.isEmpty();
        boolean itemInOffHand = offHandStack.isEmpty();

        if (!itemInMainHand && !itemInOffHand) {
            if (state.getBlock() instanceof CauldronBlock cauldronBlock && cauldronBlock.isFull(state) && state.getFluidState().is(Tags.Fluids.WATER) || player.isInWaterOrBubble()) {
                AbstractCauldronBlock cauldron = (AbstractCauldronBlock) block;
                cauldron.isFull(state);
                ItemStack InputStack = inventory.getItem(mainHandSlot);


                Optional<RecipeHolder<CleaningRecipe>> recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.CLEANING_TYPE.get(),
                        new CleaningRecipeInput(mainHandStack),
                        level
                );
                if (!recipeOptional.isPresent()) {
                    recipeOptional = recipeManager.getRecipeFor(
                            ModRecipes.CLEANING_TYPE.get(),
                            new CleaningRecipeInput(offHandStack),
                            level
                    );
                }

                recipeOptional.ifPresent(recipe -> {
                    ItemStack result = recipe.value().assemble(new CleaningRecipeInput(mainHandStack), level.registryAccess());
                    if (!player.getCooldowns().isOnCooldown(mainHandStack.getItem())){
                        InputStack.shrink(1);
                        player.getInventory().add(result);

                        player.getCooldowns().addCooldown(mainHandStack.getItem(),5);
                    }
                });
            }
        }
        else if (!itemInMainHand) {

            getRecipeOutput(level, player, inventory, mainHandStack, mainHandSlot, recipeManager);

        } else if (!itemInOffHand) {
            getRecipeOutput(level, player, inventory, offHandStack, offHandSlot, recipeManager);
        }
    }

    private static boolean cleaningAbleItemInHand(ItemStack choosedHandStack, RecipeManager recipeManager) {
        boolean result = false;
        Optional<RecipeHolder<CleaningRecipe>> recipeOptional = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(choosedHandStack),
                level
        );
        if (recipeOptional.isPresent()) {
            result  = true;
        }
        return result;
    }

    private static void getRecipeOutput(Level level, Player player, Inventory inventory, ItemStack mainHandStack, int mainHandSlot, RecipeManager recipeManager) {
        ItemStack InputStack = inventory.getItem(mainHandSlot);


        Optional<RecipeHolder<CleaningRecipe>> recipeOptional = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(mainHandStack),
                level
        );

        recipeOptional.ifPresent(recipe -> {
            ItemStack result = recipe.value().assemble(new CleaningRecipeInput(mainHandStack), level.registryAccess());
            if (!player.getCooldowns().isOnCooldown(mainHandStack.getItem())){
                InputStack.shrink(1);
                player.getInventory().add(result);

                player.getCooldowns().addCooldown(mainHandStack.getItem(),35);
            }
        });
    }
}
