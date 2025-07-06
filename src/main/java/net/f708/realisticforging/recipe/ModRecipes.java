package net.f708.realisticforging.recipe;

import net.f708.realisticforging.RealisticForging;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RealisticForging.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, RealisticForging.MODID);


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

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SticksPickingRecipe>> STICKS_PICKING_SERIALIZER =
            SERIALIZERS.register("sticks_picking", SticksPickingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SticksPickingRecipe>> STICKS_PICKING_TYPE =
            TYPES.register("sticks_picking", () -> new RecipeType<SticksPickingRecipe>() {
                @Override
                public String toString() {
                    return "sticks_picking";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TongsPickingRecipe>> TONGS_PICKING_SERIALIZER =
            SERIALIZERS.register("tongs_picking", TongsPickingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TongsPickingRecipe>> TONGS_PICKING_TYPE =
            TYPES.register("tongs_picking", () -> new RecipeType<TongsPickingRecipe>() {
                @Override
                public String toString() {
                    return "tongs_picking";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CoolingRecipe>> COOLING_SERIALIZER =
            SERIALIZERS.register("cooling", CoolingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CoolingRecipe>> COOLING_TYPE =
            TYPES.register("cooling", () -> new RecipeType<CoolingRecipe>() {
                @Override
                public String toString() {
                    return "cooling";
                }
            });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TongsGetterRecipe>> TONGS_GETTER_SERIALIZER =
            SERIALIZERS.register("tongsget", TongsGetterRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TongsGetterRecipe>> TONGS_GETTER_TYPE =
            TYPES.register("tongsget", () -> new RecipeType<TongsGetterRecipe>() {
                @Override
                public String toString() {
                    return "tongsget";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindRecipe>> GRIND_RECIPE_SERIALIZER =
            SERIALIZERS.register("grinding", GrindRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GrindRecipe>> GRIND_TYPE =
            TYPES.register("grinding", () -> new RecipeType<GrindRecipe>() {
                @Override
                public String toString() {
                    return "grind";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CuttingRecipe>> CUTTING_SERIALIZER =
            SERIALIZERS.register("cutting", CuttingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CuttingRecipe>> CUTTING_TYPE =
            TYPES.register("cutting", ()-> new RecipeType<CuttingRecipe>() {
                @Override
                public String toString() {
                    return "cutting";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CarvingRecipe>> CARVING_SERIALIZER =
            SERIALIZERS.register("carving", CarvingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CarvingRecipe>> CARVING_TYPE =
            TYPES.register("carving", ()-> new RecipeType<CarvingRecipe>() {
                @Override
                public String toString() {
                    return "carving";
                }
            });

//    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PlateSmeltingRecipe>> PLATE_SMELTING =
//            SERIALIZERS.register("plate_smelting", PlateSmeltingRecipe.Serializer::new);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }

}
