package net.f708.realisticforging.compat;

import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.recipe.ForgingRecipe;
import net.f708.realisticforging.utils.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ForgingCategory implements IRecipeCategory<ForgingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "forging");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID,
            "");

    public static final RecipeType<ForgingRecipe> FORGING_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ForgingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ForgingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SMITHINGHAMMER.get()));
    }

    @Override
    public RecipeType<ForgingRecipe> getRecipeType() {
        return FORGING_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("title.forging.recipe");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 54, 34).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 70, 34).addItemStacks(BuiltInRegistries.BLOCK.getOrCreateTag(ModTags.Blocks.FORGEABLE_BLOCK).stream().map(block -> new ItemStack(block.value().asItem())).toList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 34).addItemStack(recipe.getResultItem(null));    }

    @Override
    public void draw(ForgingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
        
    }
}
