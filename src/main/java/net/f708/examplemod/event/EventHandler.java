package net.f708.examplemod.event;

import net.f708.examplemod.TEST.ForgingInput;
import net.f708.examplemod.TEST.ForgingRecipe;
import net.f708.examplemod.TEST.ModDataComponents;
import net.f708.examplemod.TEST.ModRecipes;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.utils.AnimationHelper;
//import net.f708.examplemod.utils.ForgingProcess;
import net.f708.examplemod.utils.ForgingProcedure;
import net.f708.examplemod.utils.TickScheduler;
import net.f708.examplemod.utils.TongsPickup;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

import static net.f708.examplemod.utils.ItemProcesses.FORGING_MAP;

@EventBusSubscriber(modid = "examplemod")
public class EventHandler {


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.FURNACE)
                ||
                (event.getLevel().getBlockState(event.getPos()).is(Blocks.BLAST_FURNACE))) {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
                ItemStack MainHeldItem = event.getItemStack();
                ItemStack OffHeldItem = player.getOffhandItem();
                if (MainHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)
                        ||
                        OffHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)) {
                    AnimationHelper.playAnimation(level, player, "get_hot_ore_right");
                    TickScheduler.schedule(() -> {TongsPickup tongsPickup = new TongsPickup(event);}, 4);
                    event.setCanceled(true);
                    }
                }


        }


    }

    @SubscribeEvent
    public static void hotItemsInInventory(PlayerTickEvent.Post event) {
        HotItemsDealDamage hotItemsDealDamage = new HotItemsDealDamage(event);
    }
    @SubscribeEvent
    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return; // [[1]]

        Level level = event.getLevel();
        Player player = event.getEntity();
        Block block = level.getBlockState(event.getPos()).getBlock();
        Inventory inventory = player.getInventory();
        int mainHandSlot = inventory.selected;
        int offHandSlot = 40;

        if (!(block instanceof AnvilBlock)) return;

        boolean hasHammerInMain = inventory.getItem(mainHandSlot).is(ModItems.SMITHINGHAMMER);
        boolean hasHammerInOff = inventory.getItem(offHandSlot).is(ModItems.SMITHINGHAMMER);

        if (!hasHammerInMain && !hasHammerInOff) return; // [[1]]

        event.setCanceled(true); // [[7]]

        ItemStack handWithHammer;
        int slotWithInput;

        if (hasHammerInMain) {
            handWithHammer = inventory.getItem(mainHandSlot);
            slotWithInput = offHandSlot;
        } else {
            handWithHammer = inventory.getItem(offHandSlot);
            slotWithInput = mainHandSlot;
        }

        ItemStack inputStack = inventory.getItem(slotWithInput);
        ForgingInput container = new ForgingInput(inputStack);

        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RecipeHolder<ForgingRecipe>> optional = recipeManager.getRecipeFor(
                ModRecipes.FORGING.get(),
                container,
                level
        );

        optional.ifPresent(recipe -> {
            ItemStack result = recipe.value().assemble(container, level.registryAccess());
            if (!result.isEmpty()) {
                inventory.setItem(slotWithInput, result);
            }
        });
    }

//    @SubscribeEvent
//    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
//        Level level = event.getLevel();
//        Player player = event.getEntity();
//        Block block = level.getBlockState(event.getPos()).getBlock();
//        Inventory inventory = event.getEntity().getInventory();
//        int mainHandSlot = inventory.selected;
//        int offHandSlot = 40;
//
//
//        RecipeManager recipeManager = level.getRecipeManager();
//
//        if (block instanceof AnvilBlock){
//            if (!inventory.getItem(mainHandSlot).is(ModItems.SMITHINGHAMMER) || !inventory.getItem(offHandSlot).is(ModItems.SMITHINGHAMMER)) {
//                return;
//            }
//
//
//            ItemStack handWithHammer = null;
//            ItemStack handWithoutHammer;
//            int slotWithInput = 0;
//
//
//            event.setCanceled(true);
//            if (player.getMainHandItem().is(ModItems.SMITHINGHAMMER)) {
//                handWithHammer = inventory.getItem(mainHandSlot);
//                handWithoutHammer = inventory.getItem(offHandSlot);
//                slotWithInput = offHandSlot;
//                event.setCanceled(true);
//            }
////            else {
////                handWithHammer = inventory.getItem(offHandSlot);
////                handWithoutHammer = inventory.getItem(mainHandSlot);
////                slotWithInput = mainHandSlot;
////                event.setCanceled(true);
////            }
//
//
//            ForgingInput input = new ForgingInput(handWithHammer);
//            Optional<RecipeHolder<ForgingRecipe>> optional = recipeManager.getRecipeFor(
//                    ModRecipes.FORGING.get(),
//                    input,
//                    level
//            );
//
//            ItemStack result = optional
//                    .map(RecipeHolder::value)
//                    .map(e -> e.assemble(input, level.registryAccess()))
//                    .orElse(ItemStack.EMPTY);
//
//            if(!result.isEmpty()) {
//                inventory.setItem(slotWithInput, result);
//            }
//        }
//    }

    //    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event){
//        ForgingProcess.useOnAnvil(event);
//    }

//    @SubscribeEvent
//    public static void test(PlayerInteractEvent.RightClickBlock event) {
//        ItemStack stack = event.getItemStack();
//        ModDataComponents.applyModDataComponent(stack, StageEnum.RAW.toString());
//    }


//    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event) {
//        Player player = event.getEntity();
//        Level level = event.getLevel();
//        Block block = level.getBlockState(event.getPos()).getBlock();
//        Item mainHandItem = player.getMainHandItem().getItem();
//        Item offHandItem = player.getOffhandItem().getItem();
//        if (block instanceof AnvilBlock) {
//            if (FORGING_MAP.containsKey(offHandItem) && mainHandItem == ModItems.SMITHINGHAMMER.get()
//                    ||
//                    FORGING_MAP.containsKey(mainHandItem) && offHandItem == ModItems.SMITHINGHAMMER.get()
//            ) {
//                if (player.getCooldowns().isOnCooldown(ModItems.SMITHINGHAMMER.get())) {
//                    event.setCanceled(true);
//                    return;
//                }
//                player.getCooldowns().addCooldown(ModItems.SMITHINGHAMMER.get(), 22);
//                player.getCooldowns().addCooldown(offHandItem, 22);
//                AnimationHelper.playAnimation(level, player, "ore_hit_right");
////                TickScheduler.schedule(() -> {ForgingProcess.useOnAnvil(event);}, 8);
//                event.setCanceled(true);
//
//            }
//
//        }
//
//    }
}


    











