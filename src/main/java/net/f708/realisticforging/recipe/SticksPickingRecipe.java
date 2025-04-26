package net.f708.realisticforging.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record SticksPickingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<SticksPickingRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(SticksPickingRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SticksPickingRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.STICKS_PICKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.STICKS_PICKING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SticksPickingRecipe> {
        public static final MapCodec<SticksPickingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SticksPickingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(SticksPickingRecipe::output)
        ).apply(inst, SticksPickingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SticksPickingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, SticksPickingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, SticksPickingRecipe::output,
                        SticksPickingRecipe::new
                );

        @Override
        public MapCodec<SticksPickingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SticksPickingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
