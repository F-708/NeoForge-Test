package net.f708.realisticforging.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;


public record TongsGetterRecipe(Ingredient inputItem, ItemStack output) implements Recipe<TongsGetterRecipeInput> {


    @Override
    public boolean matches(TongsGetterRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(TongsGetterRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.TONGS_GETTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TONGS_GETTER_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<TongsGetterRecipe> {

        public static final MapCodec<TongsGetterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(TongsGetterRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(TongsGetterRecipe::output)
        ).apply(inst, TongsGetterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TongsGetterRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, TongsGetterRecipe::inputItem,
                        ItemStack.STREAM_CODEC, TongsGetterRecipe::output,
                        TongsGetterRecipe::new
                );

        @Override
        public MapCodec<TongsGetterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TongsGetterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

