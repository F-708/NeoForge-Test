package net.f708.examplemod.utils;

import net.f708.examplemod.attributes.SlowDownModifier;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.item.custom.TongsItem;
import net.f708.examplemod.recipe.*;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.WaterFluid;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.Optional;

public class Utils {

    public static boolean isHoldingHammer(PlayerInteractEvent.RightClickBlock event) {
        return event.getEntity().getMainHandItem().is(ModTags.Items.HAMMER_ITEM) || (event.getEntity().getOffhandItem().is(ModTags.Items.HAMMER_ITEM));
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
        if (isAbstractFurnaceBlockEntity(event)){
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(event.getPos());
            Optional<RecipeHolder<PickingRecipe>> recipeOptional = event.getLevel().getRecipeManager().getRecipeFor(
                    ModRecipes.PICKING_TYPE.get(),
                    new PickingRecipeInput(furnace.getItem(2)),
                    level
            );
            if (recipeOptional.isPresent()) {
                result = true;
            }
        }
        return result;
    }


    private static boolean isWaterBlockInstance(PlayerInteractEvent.RightClickBlock event) {
        boolean result = false;
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        Fluid fluid = event.getLevel().getFluidState(event.getPos()).getType();
        if ((block instanceof CauldronBlock cauldronBlock && cauldronBlock.isFull(Blocks.WATER_CAULDRON.defaultBlockState())) || fluid instanceof WaterFluid) {
            result = true;
        }
        return result;
    }

    private static boolean isForgeableBlock(PlayerInteractEvent.RightClickBlock event) {
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


    public static void slowDownPlayer(AttributeMap attributeMap, Player player, int ticks){
        attributeMap.getInstance(Attributes.MOVEMENT_SPEED).addTransientModifier(SlowDownModifier.get());
        TickScheduler.schedule(()->{
            attributeMap.getInstance(Attributes.MOVEMENT_SPEED).removeModifier(SlowDownModifier.get());
        }, ticks);
    }


}
