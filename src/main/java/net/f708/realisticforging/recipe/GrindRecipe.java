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


public record GrindRecipe(Ingredient inputItem, ItemStack output) implements Recipe<GrindRecipeInput> {


    @Override
    public boolean matches(GrindRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(GrindRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.GRIND_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GRIND_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<GrindRecipe> {

        public static final MapCodec<GrindRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(GrindRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(GrindRecipe::output)
        ).apply(inst, GrindRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GrindRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, GrindRecipe::inputItem,
                        ItemStack.STREAM_CODEC, GrindRecipe::output,
                        GrindRecipe::new
                );

        @Override
        public MapCodec<GrindRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GrindRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

