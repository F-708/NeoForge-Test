package net.f708.realisticforging.utils;

import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.item.custom.SmithingHammerItem;
import net.f708.realisticforging.recipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ConditionsHelper {

    public static boolean metSledgeHammerConditions(ServerPlayer serverPlayer, BlockPos pos, Level level){
        BlockState state = level.getBlockState(pos);
        return !serverPlayer.gameMode.getGameModeForPlayer().equals(GameType.ADVENTURE) && !state.is(BlockTags.NEEDS_STONE_TOOL)
                && !state.is(BlockTags.NEEDS_IRON_TOOL)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL) && level.mayInteract(serverPlayer, pos) && state.getDestroySpeed(level, pos) != -1;
    }

    public static boolean isHoldingHammer(Player player) {
        return player.getMainHandItem().getItem() instanceof SmithingHammerItem || (player.getOffhandItem().getItem() instanceof SmithingHammerItem ||
                (player.getMainHandItem().getItem() instanceof SmithingHammerItem || player.getOffhandItem().getItem() instanceof SmithingHammerItem));
    }

    public static boolean isHoldingForgeableItem(Player player, Level level) {
        boolean result = false;
            RecipeManager recipeManager = level.getRecipeManager();
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(ItemStackRecord.getStack(player.getOffhandItem())),
                        level);
                if (recipeOptionalOff.isPresent()) {
                    result = true;
                }
                Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(ItemStackRecord.getStack(player.getMainHandItem())),
                        level);
                if (recipeOptionalMain.isPresent()) {
                    result = true;

        }
        return result;
    }

    public static InteractionHand getHandWithForgeAble(Player player, Level level){
        boolean resultExist = false;
        boolean LH = false;
        boolean RH = false;

        InteractionHand hand;

        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.FORGING_TYPE.get(),
                new ForgingRecipeInput(ItemStackRecord.getStack(player.getMainHandItem())),
//                    new ForgingRecipeInput(player.getMainHandItem().getOrDefault(ModDataComponents.ITEM_IN_TONGS, ItemStack.EMPTY)),
                level);
            if (recipeOptionalMain.isPresent()) {
                RH = true;
            }

        Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.FORGING_TYPE.get(),
                new ForgingRecipeInput(ItemStackRecord.getStack(player.getOffhandItem())),
//                    new ForgingRecipeInput(player.getMainHandItem().getOrDefault(ModDataComponents.ITEM_IN_TONGS, ItemStack.EMPTY)),
                level);
        if (recipeOptionalOff.isPresent()) {
            LH = true;
        }

        if (RH && LH){
            switch (ConditionsHelper.getHandWithHammer(player)){
                case MAIN_HAND -> hand = InteractionHand.MAIN_HAND;
                case OFF_HAND -> hand = InteractionHand.OFF_HAND;
                case null, default -> hand = null;
            }
        } else if (RH){
            hand = InteractionHand.MAIN_HAND;
        } else if (LH){
            hand = InteractionHand.OFF_HAND;
        } else {
            hand = null;
        }

        return hand;
    }


    public static boolean isHoldingTongs(Player player) {
        return player.getMainHandItem().is(ModItems.TONGS) || (player.getOffhandItem().is(ModItems.TONGS));
    }

    public static boolean isHoldingSticks(Player player) {
        return player.getMainHandItem().is(ModItems.TWOSTICKS) || (player.getOffhandItem().is(ModItems.TWOSTICKS));
    }

    public static boolean isHoldingCarvingHammer(Player player) {
        return player.getMainHandItem().is(ModItems.CARVINGHAMMER) || (player.getOffhandItem().is(ModItems.CARVINGHAMMER));
    }

    public static boolean isHoldingChisel(Player player) {
        return player.getMainHandItem().is(ModItems.POINTCHISEL) || (player.getOffhandItem().is(ModItems.POINTCHISEL));
    }

    public static boolean isHoldingCarvingHammerInRightHand(Player player){
        boolean RH = false;
        if (isHoldingCarvingHammer(player)){
            if (player.getMainHandItem().is(ModItems.CARVINGHAMMER)){
                RH = true;
            } else {
                RH = false;
            }
        }
        return RH;
    }

    private static boolean isHoldingHotItemAbleToCool(Player player, Level level){

        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(player.getMainHandItem()),
                level);
        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOffPickable = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(ItemStackRecord.getStack(player.getOffhandItem())),
                level);
        Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMainPickable = recipeManager.getRecipeFor(
                ModRecipes.COOLING_TYPE.get(),
                new CoolingRecipeInput(ItemStackRecord.getStack(player.getMainHandItem())),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent() || recipeOptionalOffPickable.isPresent() || recipeOptionalMainPickable.isPresent()) {
            result = true;

        }
        return result;
    }

    public static boolean isHoldingCleanableItem(Player player, Level level){
        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<CleaningRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<CleaningRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.CLEANING_TYPE.get(),
                new CleaningRecipeInput(player.getMainHandItem()),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }

    public static boolean isOtherHandIsFree(Player player){
        return (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty());
    }

    public static boolean isHoldingFullTongs(Player player, Level level){
        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<TongsGetterRecipe>> recipeOptionalTongsMain = recipeManager.getRecipeFor(
                ModRecipes.TONGS_GETTER_TYPE.get(),
                new TongsGetterRecipeInput(player.getMainHandItem()),
                level);

        Optional<RecipeHolder<TongsGetterRecipe>> recipeOptionalTongsOff = recipeManager.getRecipeFor(
                ModRecipes.TONGS_GETTER_TYPE.get(),
                new TongsGetterRecipeInput(player.getOffhandItem()),
                level);


        if (recipeOptionalTongsMain.isPresent() || recipeOptionalTongsOff.isPresent()) {
            result = true;

        }
        return result;
    }

    public static boolean isHoldingGrindableItem(Player player, Level level){
        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<GrindRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.GRIND_TYPE.get(),
                new GrindRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<GrindRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.GRIND_TYPE.get(),
                new GrindRecipeInput(player.getMainHandItem()),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }

    public static boolean isHoldingCuttableItem(Player player, Level level){
        boolean result = false;
        RecipeManager recipeManager = level.getRecipeManager();

        Optional<RecipeHolder<CuttingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                ModRecipes.CUTTING_TYPE.get(),
                new CuttingRecipeInput(player.getOffhandItem()),
                level);
        Optional<RecipeHolder<CuttingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                ModRecipes.CUTTING_TYPE.get(),
                new CuttingRecipeInput(player.getMainHandItem()),
                level);
        if (recipeOptionalOff.isPresent() || recipeOptionalMain.isPresent()) {
            result = true;

        }
        return result;
    }

    public static InteractionHand getHandWithHammer(Player player){
        boolean result = false;
        InteractionHand hand;
        if (player.getMainHandItem().getItem() instanceof SmithingHammerItem){
            hand = InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof SmithingHammerItem){
            hand = InteractionHand.OFF_HAND;
        } else {
            hand = null;
        }
        return hand;
    }


    private static boolean isFurnaceGotRecipeItem(Level level, BlockPos pos){
        boolean result = false;
        Optional<RecipeHolder<TongsPickingRecipe>> tongsRecipeOptional;
        Optional<RecipeHolder<SticksPickingRecipe>> sticksRecipeOptional;
        if (isAbstractFurnaceBlockEntity(level, pos)){
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(pos);
            tongsRecipeOptional = level.getRecipeManager().getRecipeFor(
                    ModRecipes.TONGS_PICKING_TYPE.get(),
                    new TongsPickingRecipeInput(furnace.getItem(2)),
                    level
            );
            sticksRecipeOptional = level.getRecipeManager().getRecipeFor(
                    ModRecipes.STICKS_PICKING_TYPE.get(),
                    new SticksPickingRecipeInput(furnace.getItem(2)),
                    level
            );
            if (tongsRecipeOptional.isPresent() && sticksRecipeOptional.isPresent()) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isCarvingRecipeBlock(Level level, BlockPos pos){
        boolean result = true;
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RecipeHolder<CarvingRecipe>> recipeBlock = recipeManager.getRecipeFor(
                ModRecipes.CARVING_TYPE.get(), (new CarvingRecipeInput(level.getBlockState(pos).getBlock().asItem().getDefaultInstance())), level);
        if (recipeBlock.isEmpty()) {
            result = false;
        }
        return result;
    }


    public static boolean isStoneCutter(Block block) {
        return block instanceof StonecutterBlock;
    }

    public static boolean isGrindStone(Block block){
        return block instanceof GrindstoneBlock;
    }

    public static boolean isWaterCauldron(Block block) {
        boolean result = block == Blocks.WATER_CAULDRON;
        return result;
    }

    public static boolean isPlayerInWater(Player player){
        return player.isInWaterOrBubble();
    }

    public static boolean isForgeableBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(ModTags.Blocks.FORGEABLE_BLOCK);
    }

    public static boolean isForgeableBlockAvailable(Level level, BlockPos pos) {
        boolean result = false;
        if (isForgeableBlock(level, pos)) {
            if (level.getBlockState(pos.above()).is(Blocks.AIR)){
                result = true;
            }
        }
        return result;
    }

    public static boolean isAbstractFurnaceBlockEntity(Level level, BlockPos pos){
        BlockEntity block = level.getBlockEntity(pos);
        return block instanceof AbstractFurnaceBlockEntity;
    }


    private static boolean isInAir(Player player) {
        return !player.isFallFlying();
    }

    private static boolean isSleeping(Player player) {
        return !player.isSleeping();
    }

    private static boolean isPassenger(Player player) {
        return !player.isPassenger();
    }

    private static boolean isSprinting(Player player) {
        return !player.isSprinting();
    }


    public static boolean carvingRangeConditions(Player player){
        return (isHoldingChisel(player) && isHoldingCarvingHammer(player));
    }


    public static boolean isMetMicsConditions(Player player){
        return isInAir(player) && isSleeping(player) && isPassenger(player) && isSprinting(player);
    }

    public static boolean isMetForgingConditions(Level level, Player player, BlockPos pos){
        return isHoldingHammer(player) && isHoldingForgeableItem(player, level) && isForgeableBlock(level, pos) && isForgeableBlockAvailable(level, pos);
    }

    public static boolean isMetPickingConditions(Level level, Player player, BlockPos pos){
        return (isHoldingTongs(player) || isHoldingSticks(player)) && isAbstractFurnaceBlockEntity(level, pos) && isFurnaceGotRecipeItem(level, pos);
    }

    public static boolean isMetCoolingConditions(Player player, Level level){
        return isHoldingHotItemAbleToCool(player, level) && isMetMicsConditions(player);
    }

    public static boolean isMetCleaningConditions(Player player, Level level){
        return isHoldingCleanableItem(player, level) && isMetMicsConditions(player) && isOtherHandIsFree(player);
    }

    public static boolean isMetGrindingConditions(Player player, Level level, BlockPos pos){
        return  isHoldingGrindableItem(player, level) && isGrindStone(level.getBlockState(pos).getBlock()) && isMetMicsConditions(player);
    }

    public static boolean isMetCuttingConditions(Player player, Level level, BlockPos pos){
        return  isHoldingCuttableItem(player, level) && isStoneCutter(level.getBlockState(pos).getBlock()) && isMetMicsConditions(player);
    }

    public static boolean isMetSticksTongsGetterConditions(Player player, Level level){
        return isHoldingFullTongs(player, level);
    }

    public static boolean isMetCarvingConditions(Level level, Player player, BlockPos pos){
        return isHoldingChisel(player) && isHoldingCarvingHammer(player) && isCarvingRecipeBlock(level, pos) && isMetMicsConditions(player);
    }

//    public static boolean canPlace(Player player, ItemStack stack){
//        for (int i = 0; i < player.getInventory().si)
//    }





}