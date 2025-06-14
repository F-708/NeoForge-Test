package net.f708.realisticforging.datagen.builder;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.recipe.CoolingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoolingRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CoolingRecipeBuilder(Ingredient ingredient, ItemStack result, Map<String, Criterion<?>> criteria, RecipeCategory category) {
        this.ingredient = ingredient;
        this.result = result;
        this.category = category;
        this.criteria.putAll(criteria);
    }


    public static <T extends CoolingRecipe> CoolingRecipeBuilder generic(Ingredient ingredient, ItemStack result, RecipeCategory category) {
        return new CoolingRecipeBuilder(ingredient, result, new LinkedHashMap<>(), category);
    }

    public static CoolingRecipeBuilder basic(Ingredient ingredient, ItemStack result, int maxStage, Map<String, Criterion<?>> criteria, RecipeCategory category) {
        return new CoolingRecipeBuilder(ingredient, result, criteria, category);
    }







    protected static String getHasName(ItemLike itemLike) {
        return "has_" + getItemName(itemLike);
    }

    protected static String getItemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        CoolingRecipe forgingRecipe = new CoolingRecipe(this.ingredient, this.result);
        Advancement.Builder advancement$builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "cooling_" + getItemName(result.getItem())), forgingRecipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }
}
