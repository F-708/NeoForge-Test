package net.f708.realisticforging.events;

import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.network.packets.PacketPlayAnimationAtPlayer;
import net.f708.realisticforging.recipe.*;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.TickScheduler;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.Utils;
import net.f708.realisticforging.utils.animations.AnimationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;

public class ProcedureHandler {


    public static void ForgingProcedure(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (ConditionsHelper.isMetForgingConditions(level, player, event.getPos())) {
            AttributeMap attributeMap = player.getAttributes();
            Inventory inventory = player.getInventory();

            int slotWithForgeable;
            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            Optional<RecipeHolder<ForgingRecipe>> recipeOptional;
            RecipeHolder<ForgingRecipe> recipeHolder;
            ItemStack result;
            ItemStack Hammer;
            InteractionHand hand;

            if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                slotWithForgeable = inventory.selected;
                Hammer = player.getOffhandItem();
                hand = InteractionHand.OFF_HAND;
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getMainHandItem()),
                        event.getLevel());
            } else {
                Hammer = player.getMainHandItem();
                slotWithForgeable = 40;
                hand = InteractionHand.MAIN_HAND;
                recipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.FORGING_TYPE.get(),
                        new ForgingRecipeInput(player.getOffhandItem()),
                        event.getLevel());
            }
            if (recipeOptional.isPresent()) {
                if (player.getCooldowns().isOnCooldown(Hammer.getItem())){
                    event.setCanceled(true);
                    return;
                }
                recipeHolder = recipeOptional.get();
                int maxStage = recipeHolder.value().getMaxStage();
                result = recipeHolder.value().assemble(new ForgingRecipeInput(inventory.getItem(slotWithForgeable)), level.registryAccess());
//                AnimationHelper.playForgingAnimation(hand);
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PacketPPPAnimation(event.getEntity().getId(), hand));
                }
                player.getCooldowns().addCooldown(Hammer.getItem(), 45);
                event.setCanceled(true);
                BlockPos pos = event.getPos();
                Utils.slowDownPlayer(attributeMap, player, 45);
                Hammer.hurtAndBreak(1, player, Hammer.getEquipmentSlot());
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 11);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 25);
                TickScheduler.schedule(() -> {
                    Utils.playForgingSound(level, pos);
                    Utils.sendForgingParticles((ServerLevel) level, pos);
                }, 39);



                TickScheduler.schedule(() -> {
                    if (!ConditionsHelper.isMetForgingConditions(level, player, pos)) {return;}
                    int slotWithForgeableItem;
                    if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                        slotWithForgeableItem = inventory.selected;
                    } else {
                        slotWithForgeableItem = 40;
                    }
                    int currentStage = inventory.getItem(slotWithForgeableItem).getOrDefault(ModDataComponents.FORGE_STATE, 1);
                    if (currentStage < maxStage) {
                        if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableMain = inventory.selected;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getMainHandItem()),
                                    event.getLevel());
                            if (recipeOptionalMain.isPresent()) {
                                inventory.getItem(slotWithForgeableMain).set(ModDataComponents.FORGE_STATE, inventory.getItem(slotWithForgeableMain).getOrDefault(ModDataComponents.FORGE_STATE, 1) + 1);
                            }
                        } else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)){
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()){
                                inventory.getItem(slotWithForgeableOff).set(ModDataComponents.FORGE_STATE, inventory.getItem(slotWithForgeableOff).getOrDefault(ModDataComponents.FORGE_STATE, 1) + 1);
                            }
                        }
                    } else {
                        if (player.getOffhandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableMain = inventory.selected;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getMainHandItem()),
                                    event.getLevel());
                            if (recipeOptionalMain.isPresent()){
                                inventory.setItem(slotWithForgeableMain, result);
                            }
                        }
                        else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)){
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()){
                                inventory.setItem(slotWithForgeableOff, result);
                            }
                        }
                    }
                }, 42);

            }
        } else if (ConditionsHelper.isHoldingHammer(player) && ConditionsHelper.isForgeableBlock(level, event.getPos())){
            event.setCanceled(true);
        }
    }

    public static void PickingProcedure(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (ConditionsHelper.isMetPickingConditions(level, player, event.getPos())) {
            Inventory inventory = player.getInventory();
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(event.getPos());
            int slotWithHolder;
            ItemStack Holder = null;
            InteractionHand hand = null;
            RecipeManager recipeManager = event.getLevel().getRecipeManager();
            Optional<RecipeHolder<TongsPickingRecipe>> tongsRecipeOptional = Optional.empty();
            Optional<RecipeHolder<SticksPickingRecipe >> sticksRecipeOptional = Optional.empty();
            RecipeHolder<SticksPickingRecipe> sticksRecipeHolder;
            RecipeHolder<TongsPickingRecipe> tongsRecipeHolder;

            if (ConditionsHelper.isHoldingTongs(player)) {
                Holder = ModItems.TONGS.toStack();
            } else {
                Holder = ModItems.TWOSTICKS.toStack();
            }

            if (player.getMainHandItem().is(ModItems.TONGS)) {
                slotWithHolder = inventory.selected;
                hand = InteractionHand.MAIN_HAND;
                assert furnace != null;
                tongsRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_PICKING_TYPE.get(),
                        new TongsPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getOffhandItem().is(ModItems.TONGS)){
                slotWithHolder = 40;
                hand = InteractionHand.OFF_HAND;
                assert furnace != null;
                tongsRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_PICKING_TYPE.get(),
                        new TongsPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getMainHandItem().is(ModItems.TWOSTICKS)){
                slotWithHolder = inventory.selected;
                hand = InteractionHand.MAIN_HAND;
                assert furnace != null;
                sticksRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.STICKS_PICKING_TYPE.get(),
                        new SticksPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getOffhandItem().is(ModItems.TWOSTICKS)){
                slotWithHolder = 40;
                hand = InteractionHand.OFF_HAND;
                assert furnace != null;
                sticksRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.STICKS_PICKING_TYPE.get(),
                        new SticksPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else {
                slotWithHolder = inventory.selected;
            }


            if (tongsRecipeOptional.isPresent()) {
                furnace.setItem(2, ItemStack.EMPTY);
                tongsRecipeHolder = tongsRecipeOptional.get();
                ItemStack result = tongsRecipeHolder.value().assemble(new TongsPickingRecipeInput(Holder), level.registryAccess());
                AnimationHelper.playPickingAnimation(hand);
                event.setCanceled(true);
                TickScheduler.schedule(()->{
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isHoldingTongs(player)){
                        Utils.sendPickingParticles((ServerLevel) level, event.getPos());
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TONGS)){
                            slot = inventory.selected;
                        } else {
                            slot = 40;
                        }
                        inventory.setItem(slot, result);
                    }

                }, 8);
            } else if (sticksRecipeOptional.isPresent()) {
                furnace.setItem(2, ItemStack.EMPTY);
                sticksRecipeHolder = sticksRecipeOptional.get();
                ItemStack result = sticksRecipeHolder.value().assemble(new SticksPickingRecipeInput(Holder), level.registryAccess());
                AnimationHelper.playPickingAnimation(hand);
                event.setCanceled(true);
                TickScheduler.schedule(()->{
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isHoldingSticks(player)) {
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TWOSTICKS)) {
                            slot = inventory.selected;
                        } else {
                            slot = 40;
                        }
                        inventory.setItem(slot, result);
                    }
                }, 8);
            }

        } else if (ConditionsHelper.isHoldingTongs(player) || ConditionsHelper.isHoldingSticks(player) && ConditionsHelper.isAbstractFurnaceBlockEntity(level, event.getPos())){
            event.setCanceled(true);
        }
    }

    public static void CoolingProcedure (Level level, Player player, BlockPos pos){
        Inventory inventory = player.getInventory();
        if (ConditionsHelper.isMetCoolingConditions(player, level)) {
            if (ConditionsHelper.isMetMicsConditions(player)) {
                if (ConditionsHelper.isWaterCauldron(level.getBlockState(pos).getBlock()) || ConditionsHelper.isPlayerInWater(player)) {
                    RecipeManager recipeManager = level.getRecipeManager();

                    Optional<RecipeHolder<CoolingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                            ModRecipes.COOLING_TYPE.get(),
                            new CoolingRecipeInput(player.getOffhandItem()),
                            level);
                    Optional<RecipeHolder<CoolingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                            ModRecipes.COOLING_TYPE.get(),
                            new CoolingRecipeInput(player.getMainHandItem()),
                            level);

                    if (recipeOptionalOff.isPresent()) {
                        if (player.getCooldowns().isOnCooldown(player.getOffhandItem().getItem())) {return;}
                        AnimationHelper.playCoolingAnimation(InteractionHand.OFF_HAND);
                        ItemStack result = recipeOptionalOff.get().value().assemble(new CoolingRecipeInput(player.getOffhandItem()), level.registryAccess());
                        player.getCooldowns().addCooldown(player.getOffhandItem().getItem(), 20);
                        TickScheduler.schedule(() -> {
                            Utils.sendCoolingParticles((ServerLevel) level, pos);
                            Utils.playCoolingSound(level, pos);
                        }, 10);
                        TickScheduler.schedule(() -> {
                            if (ConditionsHelper.isMetCoolingConditions(player, level)) {
                                inventory.setItem(40, result);

                            }
                        }, 16);

                    } else if (recipeOptionalMain.isPresent()) {
                        if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {return;}
                        AnimationHelper.playCoolingAnimation(InteractionHand.MAIN_HAND);
                        ItemStack result = recipeOptionalMain.get().value().assemble(new CoolingRecipeInput(player.getMainHandItem()), level.registryAccess());
                        player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), 20);
                        TickScheduler.schedule(() -> {
                            Utils.sendCoolingParticles((ServerLevel) level, pos);
                            Utils.playCoolingSound(level, pos);
                        }, 10);
                        TickScheduler.schedule(() -> {
                            if (ConditionsHelper.isMetCoolingConditions(player, level)) {
                                inventory.setItem(inventory.selected, result);
                            }

                        }, 16);
                    }
                }
            }
        }
    }

    public static void CleaningProcedure (Level level, Player player){
        if (ConditionsHelper.isMetCleaningConditions(player, level)){
            if (ConditionsHelper.isMetMicsConditions(player)){
                InteractionHand hand;
                ItemStack recipeHolder;
                RecipeManager recipeManager = level.getRecipeManager();
                int slot;
                ItemStack stack;
                Optional<RecipeHolder<CleaningRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.CLEANING_TYPE.get(),
                        new CleaningRecipeInput(player.getMainHandItem()),
                        level);
                if (recipeOptionalMain.isPresent()){
                    stack = player.getMainHandItem();
                    hand = InteractionHand.MAIN_HAND;
                    recipeHolder = recipeOptionalMain.get().value().assemble(new CleaningRecipeInput(player.getMainHandItem()), level.registryAccess());
                    slot = player.getInventory().selected;
                } else {
                    stack = player.getOffhandItem();
                    slot = 40;
                    hand = InteractionHand.OFF_HAND;
                    recipeOptionalMain = recipeManager.getRecipeFor(
                            ModRecipes.CLEANING_TYPE.get(),
                            new CleaningRecipeInput(player.getOffhandItem()),
                            level);
                    recipeHolder = recipeOptionalMain.get().value().assemble(new CleaningRecipeInput(player.getOffhandItem()), level.registryAccess());
                }
                if (player.getCooldowns().isOnCooldown(stack.getItem())) {return;}
                AnimationHelper.playCleaningAnimationBareHands(hand);
                player.getCooldowns().addCooldown(stack.getItem(), 40);
                Utils.sendCleaningParticles((ServerLevel) level, player);
                Utils.playCleaningSound((ServerLevel) level, player);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isMetCleaningConditions(player, level)) {

                        player.getInventory().removeItem(Utils.getCleanableItemSlot(level, player), 1);
                        player.getInventory().add(recipeHolder);

                    }
                }, 35);

            }
        }

    }

    public static void GrindingProcedure(Level level, Player player, BlockPos pos){
        if (ConditionsHelper.isMetGrindingConditions(player, level, level.getBlockState(pos).getBlock())){
            if (ConditionsHelper.isMetMicsConditions(player)){
                InteractionHand hand = InteractionHand.OFF_HAND;
                int slot = 40;
                Inventory inventory = player.getInventory();
                RecipeManager recipeManager = level.getRecipeManager();

                Optional<RecipeHolder<GrindRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.GRIND_TYPE.get(),
                        new GrindRecipeInput(player.getMainHandItem()),
                        level);

                Optional<RecipeHolder<GrindRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.GRIND_TYPE.get(),
                        new GrindRecipeInput(player.getOffhandItem()),
                        level);
            }
        }
    }

    public static void SticksTongsGetterProcedure(Level level, Player player){
        if (ConditionsHelper.isMetSticksTongsGetterConditions(player, level)){
            if (ConditionsHelper.isMetMicsConditions(player)){
                InteractionHand hand = InteractionHand.OFF_HAND;
                int slot = 40;
                Inventory inventory = player.getInventory();

                RecipeManager recipeManager = level.getRecipeManager();

                Optional<RecipeHolder<TongsGetterRecipe>> recipeOptionalTongsMain = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_GETTER_TYPE.get(),
                        new TongsGetterRecipeInput(player.getMainHandItem()),
                        level);

                Optional<RecipeHolder<TongsGetterRecipe>> recipeOptionalTongsOff = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_GETTER_TYPE.get(),
                        new TongsGetterRecipeInput(player.getOffhandItem()),
                        level);

                if (recipeOptionalTongsMain.isPresent()){
                    hand = InteractionHand.MAIN_HAND;
                    inventory.add(recipeOptionalTongsMain.get().value().assemble(new TongsGetterRecipeInput(player.getMainHandItem()), level.registryAccess()));
                    inventory.setItem(inventory.selected, ModItems.TONGS.toStack());
                } else if (recipeOptionalTongsOff.isPresent()){
                        inventory.add(recipeOptionalTongsOff.get().value().assemble(new TongsGetterRecipeInput(player.getOffhandItem()), level.registryAccess()));
                        inventory.setItem(40, ModItems.TONGS.toStack());
                    }
                AnimationHelper.playSticksTongsGettingAnimation(hand);
                }
        }

            }
        }

