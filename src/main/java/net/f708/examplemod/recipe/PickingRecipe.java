package net.f708.examplemod.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record PickingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<PickingRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(PickingRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(PickingRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.PICKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PICKING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PickingRecipe> {
        public static final MapCodec<PickingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PickingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(PickingRecipe::output)
        ).apply(inst, PickingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PickingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, PickingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, PickingRecipe::output,
                        PickingRecipe::new
                );

        @Override
        public MapCodec<PickingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PickingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
