package net.f708.examplemod.utils;

import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.recipe.GrowthChamberRecipe;
import net.f708.examplemod.recipe.GrowthChamberRecipeInput;
import net.f708.examplemod.recipe.ModRecipes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.swing.text.html.Option;
import java.util.Optional;

import static net.f708.examplemod.recipe.ModRecipes.GROWTH_CHAMBER_TYPE;

public class ForgingProcedure {


    private Level level;
    private ItemStack stack;

    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getLevel();
        Player player = event.getEntity();
        Block block = level.getBlockState(event.getPos()).getBlock();
        Inventory inventory = event.getEntity().getInventory();
        int mainHandSlot = inventory.selected;
        int offHandSlot = 40;
        int slotWithInput;
        ItemStack handWithHammer;
        ItemStack handWithoutHammer;



        if (block instanceof AnvilBlock){
            if (!inventory.getItem(mainHandSlot).is(ModItems.SMITHINGHAMMER) && !inventory.getItem(offHandSlot).is(ModItems.SMITHINGHAMMER)) {
                return;
            }
            event.setCanceled(true);
            if (player.getMainHandItem().is(ModItems.SMITHINGHAMMER)) {
                handWithHammer = inventory.getItem(mainHandSlot);
                handWithoutHammer = inventory.getItem(offHandSlot);
                slotWithInput = offHandSlot;
            }
            else {
                handWithHammer = inventory.getItem(offHandSlot);
                handWithoutHammer = inventory.getItem(mainHandSlot);
                slotWithInput = mainHandSlot;
            }


            if (player.isShiftKeyDown()){
                return;
            }
            if(!level.isClientSide){
                Optional<RecipeHolder<GrowthChamberRecipe>> recipe =
                        level.getRecipeManager().getRecipeFor(ModRecipes.GROWTH_CHAMBER_TYPE.get(), new GrowthChamberRecipeInput(new ItemStack(handWithoutHammer.getItem())), level);
                ItemStack output = recipe.get().value().output();
                if(recipe.isEmpty()){
                    return;
                }
                inventory.setItem(slotWithInput, output);
                event.setCanceled(true);
            }








        }


    }

    private boolean hasRecipe(){
        Optional<RecipeHolder<GrowthChamberRecipe>> recipe = getCurrentRecipe(stack, level);
        if (recipe.isEmpty()){
            return false;
        }

        ItemStack output = recipe.get().value().output();
        return true;
    }
    private Optional<RecipeHolder<GrowthChamberRecipe>> getCurrentRecipe(ItemStack stack, Level level) {
        return this.level.getRecipeManager()
                .getRecipeFor(GROWTH_CHAMBER_TYPE.get(), new GrowthChamberRecipeInput(stack), level);
    }

    private void craftItem(){
        Optional<RecipeHolder<GrowthChamberRecipe>> recipe = getCurrentRecipe(stack, level);
        ItemStack output = recipe.get().value().output();
    }


}
