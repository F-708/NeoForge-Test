package net.f708.realisticforging.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;

public record CarvingRecipe(Ingredient inputItem, ItemStack itemOutput, int hitAmount, ItemStack outputBlock) implements Recipe<CarvingRecipeInput> {

    public ItemStack getInputItem(){
        return Arrays.stream(inputItem.getItems()).toList().stream().findFirst().get();
    }

    public ItemStack getOutPutBlock(){
        return outputBlock;
    }

    public ItemStack getResultItem(){
        return itemOutput.copy();
    }

    public int getHitAmount(){
        return hitAmount;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(CarvingRecipeInput carvingRecipeInput, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(carvingRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(CarvingRecipeInput input, HolderLookup.Provider registries) {
        return itemOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return itemOutput;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CARVING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CARVING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CarvingRecipe> {

        public static final MapCodec<CarvingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CarvingRecipe::inputItem),
                ItemStack.CODEC.optionalFieldOf("resultitem", ItemStack.EMPTY).forGetter(CarvingRecipe::itemOutput),
                Codec.INT.fieldOf("hit_amount").forGetter(CarvingRecipe::hitAmount),
                ItemStack.CODEC.fieldOf("resultblock").forGetter(CarvingRecipe::outputBlock)
        ).apply(inst, CarvingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CarvingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CarvingRecipe::inputItem,
                        ItemStack.OPTIONAL_STREAM_CODEC, CarvingRecipe::itemOutput,
                        ByteBufCodecs.INT, CarvingRecipe::hitAmount,
                        ItemStack.STREAM_CODEC, CarvingRecipe::outputBlock,
                        CarvingRecipe::new
                );

        @Override
        public MapCodec<CarvingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CarvingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}