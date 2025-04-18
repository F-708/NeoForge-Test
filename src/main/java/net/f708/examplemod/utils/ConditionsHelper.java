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

    public static boolean isHoldingHammer(PlayerInteractEvent.RightClickBlock event) {
        return event.getEntity().getMainHandItem().is(ModTags.Items.HAMMER_ITEM) || (event.getEntity().getOffhandItem().is(ModTags.Items.HAMMER_ITEM));
    }

    public static boolean isHoldingForgeableItem(PlayerInteractEvent.RightClickBlock event) {
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

    public static boolean isHoldingTongs(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        return player.getMainHandItem().is(ModItems.TONGS) || (player.getOffhandItem().is(ModItems.TONGS));
    }

    public static boolean isHoldingSticks(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        return player.getMainHandItem().is(ModItems.TWOSTICKS) || (player.getOffhandItem().is(ModItems.TWOSTICKS));
    }

    private static boolean isFurnaceGotRecipeItem(PlayerInteractEvent.RightClickBlock event){
        Level level = event.getLevel();
        boolean result = false;
        Optional<RecipeHolder<TongsPickingRecipe>> tongsRecipeOptional;
        Optional<RecipeHolder<SticksPickingRecipe>> sticksRecipeOptional;
        if (isAbstractFurnaceBlockEntity(event)){
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(event.getPos());
            tongsRecipeOptional = event.getLevel().getRecipeManager().getRecipeFor(
                    ModRecipes.TONGS_PICKING_TYPE.get(),
                    new TongsPickingRecipeInput(furnace.getItem(2)),
                    level
            );
            sticksRecipeOptional = event.getLevel().getRecipeManager().getRecipeFor(
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

    private static boolean isHoldingHotItemAbleToCool(PlayerInteractEvent.RightClickBlock event){

        boolean result = false;
        RecipeManager recipeManager = event.getLevel().getRecipeManager();

        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(event.getEntity().getOffhandItem()),
                event.getLevel());
        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(event.getEntity().getMainHandItem()),
                event.getLevel());
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }



    private static boolean isWaterBlockInstance(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();

        boolean result = false;

        if (player.isInWaterOrBubble()) {
            result = true;
        }
            if(block == Blocks.WATER_CAULDRON){
                result = true;
            }
        return result;
    }

    public static boolean isForgeableBlock(PlayerInteractEvent.RightClickBlock event) {
        return event.getLevel().getBlockState(event.getPos()).is(ModTags.Blocks.FORGEABLE_BLOCK);
    }

    private static boolean isAbstractFurnaceBlockEntity(PlayerInteractEvent.RightClickBlock event){
        return event.getLevel().getBlockEntity(event.getPos()) instanceof AbstractFurnaceBlockEntity;
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


    public static boolean isMetForgingConditions(PlayerInteractEvent.RightClickBlock event){
        return isHoldingHammer(event)
                && isHoldingForgeableItem(event)
                && isForgeableBlock(event) && isInAir(event) && isSleeping(event) && isPassenger(event) && isSprinting(event);
    }

    public static boolean isMetPickingConditions(PlayerInteractEvent.RightClickBlock event){
        return isHoldingTongs(event) || isHoldingSticks(event) && isAbstractFurnaceBlockEntity(event) && isFurnaceGotRecipeItem(event) && isInAir(event) && isSleeping(event) && isPassenger(event) && isSprinting(event);
    }

    public static boolean isMetCoolingConditions(PlayerInteractEvent.RightClickBlock event){
        return
//                isHoldingHotItemAbleToCool(event)
//                        &&
                isWaterBlockInstance(event)
                &&
        isSleeping(event) && isPassenger(event) && isSprinting(event);
    }





}
