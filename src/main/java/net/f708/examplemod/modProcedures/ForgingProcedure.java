package net.f708.examplemod.modProcedures;

import net.f708.examplemod.attributes.SlowDownModifier;
import net.f708.examplemod.component.ModDataComponents;
import net.f708.examplemod.recipe.ForgingRecipe;
import net.f708.examplemod.recipe.ForgingRecipeInput;
import net.f708.examplemod.recipe.ModRecipes;
import net.f708.examplemod.utils.AnimationHelper;
import net.f708.examplemod.utils.Utils;
import net.f708.examplemod.utils.ModTags;
import net.f708.examplemod.utils.TickScheduler;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;


public class ForgingProcedure {



    public static void accept(PlayerInteractEvent.RightClickBlock event) {
        if (Utils.isMetForgingConditions(event)) {
            Player player = event.getEntity();
            AttributeMap attributeMap = player.getAttributes();
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
                Utils.slowDownPlayer(attributeMap, player, 45);
                TickScheduler.schedule(() -> {
                    if (currentStage >= maxStage) {
                        inventory.setItem(slotWithForgeable, result);
                    } else{
                        inventory.getItem(slotWithForgeable).set(ModDataComponents.FORGE_STATE, currentStage + 1);
                    }
                }, 45);

            }
        } else if (Utils.isHoldingHammer(event)){
            event.setCanceled(true);
        }
    }

}
