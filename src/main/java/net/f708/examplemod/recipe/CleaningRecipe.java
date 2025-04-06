package net.f708.examplemod.recipe;

import com.mojang.serialization.Codec;
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

public record CleaningRecipe(Ingredient inputItem, ItemStack output) implements Recipe<CleaningRecipeInput> {


    @Override
    public boolean matches(CleaningRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(CleaningRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.CLEANING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CLEANING_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<CleaningRecipe> {

        public static final MapCodec<CleaningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CleaningRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CleaningRecipe::output)
        ).apply(inst, CleaningRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CleaningRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CleaningRecipe::inputItem,
                        ItemStack.STREAM_CODEC, CleaningRecipe::output,
                        CleaningRecipe::new
                );

        @Override
        public MapCodec<CleaningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CleaningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
