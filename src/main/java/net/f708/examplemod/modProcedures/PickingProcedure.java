package net.f708.examplemod.modProcedures;

import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.recipe.*;
import net.f708.examplemod.utils.AnimationHelper;
import net.f708.examplemod.utils.ModTags;
import net.f708.examplemod.utils.TickScheduler;
import net.f708.examplemod.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.Random;

public class PickingProcedure {

    public static void accept(PlayerInteractEvent.RightClickBlock event){
        if (Utils.isMetPickingConditions(event)) {
            Player player = event.getEntity();
            Level level = event.getLevel();
            Inventory inventory = player.getInventory();
            BlockEntity blockEntity = level.getBlockEntity(event.getPos());
            int slotWithHolder = 40;
            String animation;
            ItemStack Holder = null;

            if (Utils.isHoldingTongs(event)) {
                Holder = ModItems.TONGS.get().getDefaultInstance();
            } else {
                Holder = ModItems.TWOSTICKS.get().getDefaultInstance();
            }

            if (player.getMainHandItem().is(Holder.getItem())){
                slotWithHolder = inventory.selected;
                animation = "picking_ore_right";
            } else {
                animation = "picking_ore_left";
            }



            AnimationHelper.playAnimation(level, player, animation);



        } else if (Utils.isHoldingTongs(event) || Utils.isHoldingSticks(event)){
            event.setCanceled(true);
        }
    }


//        private static int slotWithTongs;
//        private static String animation;
//        private static InteractionHand interactionHand;
//
//
//    public static void pickHotItemTongs(PlayerInteractEvent.RightClickBlock event){
//
//        if (event.getLevel().isClientSide()) return;
//
//            Player player = event.getEntity();
//            BlockPos pos = event.getPos();
//            Level level = event.getLevel();
//            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
//            if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
//                ItemStack MainHeldItem = event.getItemStack();
//                ItemStack OffHeldItem = player.getOffhandItem();
//
//
//                RecipeManager recipeManager = level.getRecipeManager();
//
//                if (MainHeldItem.is(ModItems.TONGS)){
//                    slotWithTongs = player.getInventory().selected;
//                    animation = "get_hot_ore_right";
//                    interactionHand = InteractionHand.OFF_HAND;
//
//                } else if (OffHeldItem.is(ModItems.TONGS)) {
//                    slotWithTongs = 40;
//                    interactionHand = InteractionHand.OFF_HAND;
//                } else if (MainHeldItem.is(ModItems.TONGS) && (OffHeldItem.is(ModItems.TONGS))) {
//                    slotWithTongs = player.getInventory().selected;
//                    interactionHand = InteractionHand.MAIN_HAND;
//                } else if (!MainHeldItem.is(ModItems.TONGS) && !(OffHeldItem.is(ModItems.TONGS))) {
//                    return;
//                }
//                Optional<RecipeHolder<PickingRecipe>> recipeOptional = recipeManager.getRecipeFor(
//                        ModRecipes.PICKING_TYPE.get(),
//                        new PickingRecipeInput(furnace.getItem(2)),
//                        level
//                );
//
//                recipeOptional.ifPresent(recipe -> {
//                           ItemStack result = recipe.value().getResultItem(level.registryAccess());
//                    event.setCanceled(true);
//                        AnimationHelper.playAnimation(level, player, animation);
//                    TickScheduler.schedule(() -> {
//
//                        player.getInventory().setItem(slotWithTongs, result);
//                        furnace.setItem(2, ItemStack.EMPTY);
//                        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) * 0.5F);
//                        Random random = new Random();
//                        ((ServerLevel) level).sendParticles(
//                                ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, random.nextInt(3), 0, 0.1, 0, 0.05);
//                    }, 8);
//                });
//            }
//    }
}
