package net.f708.realisticforging.datagen;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.datagen.builder.ForgingRecipeBuilder;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.IRON_SLEDGEHAMMER.get())
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', Items.IRON_BLOCK)
                .define('S', Items.STICK)
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DIAMOND_SLEDGEHAMMER.get())
                .pattern(" D ")
                .pattern(" S ")
                .pattern(" S ")
                .define('D', Items.DIAMOND_BLOCK)
                .define('S', Items.STICK)
                .unlockedBy("has_diamond_block", has(Items.DIAMOND_BLOCK)).save(recipeOutput);

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.DIAMOND_SLEDGEHAMMER.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, ModItems.NETHERITE_SLEDGEHAMMER.get());

        itemForging(recipeOutput, Items.NETHER_BRICK, Items.DIAMOND_BLOCK.getDefaultInstance(), 20,  RecipeCategory.TOOLS);

    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }


    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, RealisticForging.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void itemForging(RecipeOutput recipeOutput, ItemLike ingredient, ItemStack output, int maxStage, RecipeCategory category){
            ForgingRecipeBuilder.generic(Ingredient.of(ingredient), output, maxStage, category).unlockedBy(getHasName(ingredient), has(ingredient))
                    .save(recipeOutput, RealisticForging.MODID + ":" + getItemName(output.getItem()) + "forging" + "_" + getItemName(output.getItem()));

    }
}
