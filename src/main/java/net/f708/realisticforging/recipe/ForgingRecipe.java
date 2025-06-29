package net.f708.realisticforging.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ForgingRecipe implements Recipe<ForgingRecipeInput> {

    protected Ingredient inputItem;
    protected ItemStack output;
    protected int maxStage;

    public ForgingRecipe(Ingredient inputItem, ItemStack output, int maxStage) {
        this.inputItem = inputItem;
        this.output = output;
        this.maxStage = maxStage;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Integer getMaxStage(){
        return maxStage;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ForgingRecipeInput forgingRecipeInput, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return inputItem.test(forgingRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ForgingRecipeInput input, HolderLookup.Provider registries) {
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
        return ModRecipes.FORGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FORGING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ForgingRecipe> {

        public static final MapCodec<ForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ForgingRecipe::getInputItem),
                ItemStack.CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(ForgingRecipe::getOutput),
                Codec.INT.fieldOf("max_stage").forGetter(ForgingRecipe::getMaxStage)
        ).apply(inst, ForgingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ForgingRecipe::getInputItem,
                        ItemStack.OPTIONAL_STREAM_CODEC, ForgingRecipe::getOutput,
                        ByteBufCodecs.INT, ForgingRecipe::getMaxStage,
                        ForgingRecipe::new
                );

        @Override
        public MapCodec<ForgingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
