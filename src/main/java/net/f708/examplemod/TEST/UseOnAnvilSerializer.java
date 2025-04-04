//package net.f708.examplemod.TEST;
//
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.f708.examplemod.ExampleMod;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//import net.neoforged.neoforge.registries.DeferredRegister;
//
//import java.util.function.Supplier;
//
//public class UseOnAnvilSerializer implements RecipeSerializer<UseOnAnvilRecipe> {
//    public static final MapCodec<UseOnAnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
//            BlockState.CODEC.fieldOf("inputState").forGetter(UseOnAnvilRecipe::getInputState),
//            Ingredient.CODEC.fieldOf("inputItem").forGetter(UseOnAnvilRecipe::getInputItem),
//            ItemStack.CODEC.fieldOf("result").forGetter(UseOnAnvilRecipe::getResult)
//    ).apply(inst, UseOnAnvilRecipe::new));
//    public static final StreamCodec<RegistryFriendlyByteBuf, UseOnAnvilRecipe> STREAM_CODEC =
//            StreamCodec.composite(
//                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), UseOnAnvilRecipe::getInputState,
//                    Ingredient.CONTENTS_STREAM_CODEC, UseOnAnvilRecipe::getInputItem,
//                    ItemStack.STREAM_CODEC, UseOnAnvilRecipe::getResult,
//                    UseOnAnvilRecipe::new
//            );
//
//    // Return our map codec.
//    @Override
//    public MapCodec<UseOnAnvilRecipe> codec() {
//        return CODEC;
//    }
//
//    // Return our stream codec.
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, UseOnAnvilRecipe> streamCodec() {
//        return STREAM_CODEC;
//    }
//
//    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
//            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExampleMod.MODID);
//
//    public static final Supplier<RecipeSerializer<UseOnAnvilRecipe>> USE_ON_ANVIL =
//            RECIPE_SERIALIZERS.register("right_click_block", UseOnAnvilSerializer::new);
//
//}
