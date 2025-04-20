package net.f708.examplemod.utils;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.recipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class ConditionsHelper {

    public static boolean isHoldingHammer(Player player) {
        return player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM) || (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM));
    }

    public static boolean isHoldingForgeableItem(Player player, Level level) {
        boolean result = false;
        if (isHoldingHammer(player)) {

            RecipeManager recipeManager = level.getRecipeManager();
            if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)) {
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getOffhandItem()),
                        level);
                if (recipeOptionalOff.isPresent()) {
                    result = true;
                }
            } else {
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getMainHandItem()),
                        level);
                if (recipeOptionalMain.isPresent()) {
                    result = true;
                }
            }
        }

        return result;
    }

    public static boolean isHoldingTongs(Player player) {
        return player.getMainHandItem().is(ModItems.TONGS) || (player.getOffhandItem().is(ModItems.TONGS));
    }

    public static boolean isHoldingSticks(Player player) {
        return player.getMainHandItem().is(ModItems.TWOSTICKS) || (player.getOffhandItem().is(ModItems.TWOSTICKS));
    }

    private static boolean isHoldingHotItemAbleToCool(Player player, Level level){

        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(player.getMainHandItem()),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }

    public static boolean isHoldingCleanableItem(Player player, Level level){
        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<CleaningRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<CleaningRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(player.getMainHandItem()),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }


    private static boolean isFurnaceGotRecipeItem(Level level, BlockPos pos){
        boolean result = false;
        Optional<RecipeHolder<TongsPickingRecipe>> tongsRecipeOptional;
        Optional<RecipeHolder<SticksPickingRecipe>> sticksRecipeOptional;
        if (isAbstractFurnaceBlockEntity(level, pos)){
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(pos);
            tongsRecipeOptional = level.getRecipeManager().getRecipeFor(
                    ModRecipes.TONGS_PICKING_TYPE.get(),
                    new TongsPickingRecipeInput(furnace.getItem(2)),
                    level
            );
            sticksRecipeOptional = level.getRecipeManager().getRecipeFor(
                    ModRecipes.STICKS_PICKING_TYPE.get(),
                    new SticksPickingRecipeInput(furnace.getItem(2)),
                    level
            );
            if (tongsRecipeOptional.isPresent() && sticksRecipeOptional.isPresent()) {
                result = true;
            }
        }
        return result;
    }


    public static boolean isWaterCauldron(Block block) {
        boolean result = block == Blocks.WATER_CAULDRON;
        return result;
    }

    public static boolean isPlayerInWater(Player player){
        return player.isInWaterOrBubble();
    }

    public static boolean isForgeableBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(ModTags.Blocks.FORGEABLE_BLOCK);
    }

    private static boolean isAbstractFurnaceBlockEntity(Level level, BlockPos pos){
        return level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity;
    }


    private static boolean isInAir(Player player) {
        return !player.isFallFlying();
    }

    private static boolean isSleeping(Player player) {
        return !player.isSleeping();
    }

    private static boolean isPassenger(Player player) {
        return !player.isPassenger();
    }

    private static boolean isSprinting(Player player) {
        return !player.isSprinting();
    }

    public static boolean isMetMicsConditions(Player player){
        return isInAir(player) && isSleeping(player) && isPassenger(player) && isSprinting(player);
    }

    public static boolean isMetForgingConditions(Level level, Player player, BlockPos pos){
        return isHoldingHammer(player)
                && isHoldingForgeableItem(player, level)
                && isForgeableBlock(level, pos);
    }

    public static boolean isMetPickingConditions(Level level, Player player, BlockPos pos){
        return isHoldingTongs(player) || isHoldingSticks(player) && isAbstractFurnaceBlockEntity(level, pos) && isFurnaceGotRecipeItem(level, pos);
    }

    public static boolean isMetCoolingConditions(Player player, Level level){
        return
                isHoldingHotItemAbleToCool(player, level);
    }





}
