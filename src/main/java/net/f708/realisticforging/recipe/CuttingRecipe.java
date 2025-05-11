package net.f708.realisticforging.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CuttingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<CuttingRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(CuttingRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(CuttingRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CUTTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CUTTING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CuttingRecipe> {
        public static final MapCodec<CuttingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CuttingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CuttingRecipe::output)
        ).apply(inst, CuttingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CuttingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CuttingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, CuttingRecipe::output,
                        CuttingRecipe::new
                );

        @Override
        public MapCodec<CuttingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CuttingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
