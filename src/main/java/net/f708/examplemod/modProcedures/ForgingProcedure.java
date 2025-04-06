package net.f708.examplemod.modProcedures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.recipe.ForgingRecipe;
import net.f708.examplemod.recipe.ForgingRecipeInput;
import net.f708.examplemod.recipe.ModRecipes;
import net.f708.examplemod.utils.AnimationHelper;
import net.f708.examplemod.utils.ModTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;


public class ForgingProcedure {

    static int maxStage;
    static int currentState;

    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;

        Level level = event.getLevel();
        Player player = event.getEntity();
        Block block = level.getBlockState(event.getPos()).getBlock();
        Inventory inventory = player.getInventory();
        ItemStack handWithHammer = null;
        int mainHandSlot = inventory.selected;
        int offHandSlot = 40;

        if (!(block instanceof AnvilBlock)) return;


        boolean hasHammerInMain = inventory.getItem(mainHandSlot).is(ModTags.Items.HAMMER_ITEM);
        boolean hasHammerInOff = inventory.getItem(offHandSlot).is(ModTags.Items.HAMMER_ITEM);
        if (hasHammerInMain){
            handWithHammer = inventory.getItem(mainHandSlot);
        } else if (hasHammerInOff){
            handWithHammer = inventory.getItem(offHandSlot);
        }

        if (!hasHammerInMain && !hasHammerInOff) return;

        int slotWithInput = hasHammerInMain ? offHandSlot : mainHandSlot;
        ItemStack inputStack = inventory.getItem(slotWithInput);


        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RecipeHolder<ForgingRecipe>> recipeOptional = recipeManager.getRecipeFor(
                ModRecipes.FORGING_TYPE.get(),
                new ForgingRecipeInput(inputStack, maxStage),
                level
        );

        recipeOptional.ifPresent(recipe -> {
            maxStage = recipe.value().getMaxStage();
            currentState = inputStack.getOrDefault(ModDataComponents.FORGE_STATE, 1);
            if (currentState < maxStage) {
                inputStack.set(ModDataComponents.FORGE_STATE, currentState + 1);
            } else {
                ItemStack result = recipe.value().getResultItem(level.registryAccess());
                inventory.setItem(slotWithInput, result);
                event.setCanceled(true);
            }


        });

    }
    public static float getStateInteger(){
        float result = 0;
        if (currentState > 1){
            result = currentState / 10f;
        }
        if (currentState > 10){
            result = result / 100f;
        }
        return result;
    }
}
