package net.f708.examplemod.recipe;

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

public record CoolingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<CoolingRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(CoolingRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(CoolingRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.COOLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COOLING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CoolingRecipe> {
        public static final MapCodec<CoolingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CoolingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CoolingRecipe::output)
        ).apply(inst, CoolingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CoolingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CoolingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, CoolingRecipe::output,
                        CoolingRecipe::new
                );

        @Override
        public MapCodec<CoolingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoolingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
