package net.f708.examplemod.recipe;

import net.f708.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExampleMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ExampleMod.MODID);


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgingRecipe>> FORGING_SERIALIZER =
            SERIALIZERS.register("forging", ForgingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgingRecipe>> FORGING_TYPE =
            TYPES.register("forging", ()-> new RecipeType<ForgingRecipe>() {
                @Override
                public String toString() {
                    return "forging";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CleaningRecipe>> CLEANING_SERIALIZER =
            SERIALIZERS.register("cleaning", CleaningRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CleaningRecipe>> CLEANING_TYPE =
            TYPES.register("cleaning", ()-> new RecipeType<CleaningRecipe>() {
                @Override
                public String toString() {
                    return "cleaning";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PickingRecipe>> PICKING_SERIALIZER =
            SERIALIZERS.register("picking", PickingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PickingRecipe>> PICKING_TYPE =
            TYPES.register("picking", () -> new RecipeType<PickingRecipe>() {
                @Override
                public String toString() {
                    return "picking";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }

}
