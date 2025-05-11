package net.f708.realisticforging.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.block.ModBlocks;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.recipe.*;
import net.f708.realisticforging.utils.*;
import net.f708.realisticforging.utils.animations.AnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcedureHandler {

    public static boolean CARVING_ACTION;

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
                if (player.getCooldowns().isOnCooldown(Hammer.getItem())) {
                    event.setCanceled(true);
                    return;
                }
                recipeHolder = recipeOptional.get();
                int maxStage = recipeHolder.value().getMaxStage();
                result = recipeHolder.value().assemble(new ForgingRecipeInput(inventory.getItem(slotWithForgeable)), level.registryAccess());
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PacketPPPAnimation(event.getEntity().getId(), hand, Animation.FORGING));
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
                    if (!ConditionsHelper.isMetForgingConditions(level, player, pos)) {
                        return;
                    }
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
                        } else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()) {
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
                            if (recipeOptionalMain.isPresent()) {
                                inventory.setItem(slotWithForgeableMain, result);
                            }
                        } else if (player.getMainHandItem().is(ModTags.Items.HAMMER_ITEM)) {
                            int slotWithForgeableOff = 40;
                            Optional<RecipeHolder<ForgingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                                    ModRecipes.FORGING_TYPE.get(),
                                    new ForgingRecipeInput(player.getOffhandItem()),
                                    event.getLevel());
                            if (recipeOptionalOff.isPresent()) {
                                inventory.setItem(slotWithForgeableOff, result);
                            }
                        }
                    }
                }, 42);

            }
        } else if (ConditionsHelper.isHoldingHammer(player) && ConditionsHelper.isForgeableBlock(level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void PickingProcedure(PlayerInteractEvent.RightClickBlock event) {
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
            Optional<RecipeHolder<SticksPickingRecipe>> sticksRecipeOptional = Optional.empty();
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
            } else if (player.getOffhandItem().is(ModItems.TONGS)) {
                slotWithHolder = 40;
                hand = InteractionHand.OFF_HAND;
                assert furnace != null;
                tongsRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.TONGS_PICKING_TYPE.get(),
                        new TongsPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getMainHandItem().is(ModItems.TWOSTICKS)) {
                slotWithHolder = inventory.selected;
                hand = InteractionHand.MAIN_HAND;
                assert furnace != null;
                sticksRecipeOptional = recipeManager.getRecipeFor(
                        ModRecipes.STICKS_PICKING_TYPE.get(),
                        new SticksPickingRecipeInput(furnace.getItem(2)),
                        event.getLevel());
            } else if (player.getOffhandItem().is(ModItems.TWOSTICKS)) {
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
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PacketPPPAnimation(event.getEntity().getId(), hand, Animation.PICKING));
                }
                event.setCanceled(true);
                TickScheduler.schedule(() -> {
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                TickScheduler.schedule(() -> {
                    if (ConditionsHelper.isHoldingTongs(player)) {
                        Utils.sendPickingParticles((ServerLevel) level, event.getPos());
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TONGS)) {
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
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PacketPPPAnimation(event.getEntity().getId(), hand, Animation.PICKING));
                }
                event.setCanceled(true);
                TickScheduler.schedule(() -> {
                    Utils.playPickingSound(level, event.getPos());
                }, 4);
                        int slot;
                        if (player.getMainHandItem().is(ModItems.TWOSTICKS)) {
                            slot = inventory.selected;
                        } else {
                            slot = 40;
                        }
                        inventory.setItem(slot, result);
            }

        } else if ((ConditionsHelper.isHoldingTongs(player) || ConditionsHelper.isHoldingSticks(player)) && ConditionsHelper.isAbstractFurnaceBlockEntity(level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void CoolingProcedure(Level level, Player player, BlockPos pos) {
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
                        if (player.getCooldowns().isOnCooldown(player.getOffhandItem().getItem())) {
                            return;
                        }
                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), InteractionHand.OFF_HAND, Animation.COOLING));
                        }
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
                        if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                            return;
                        }
                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), InteractionHand.MAIN_HAND, Animation.COOLING));
                        }
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

    public static void CleaningProcedure(Level level, Player player) {
        if (ConditionsHelper.isMetCleaningConditions(player, level)) {
            if (ConditionsHelper.isMetMicsConditions(player)) {
                InteractionHand hand;
                ItemStack recipeHolder;
                RecipeManager recipeManager = level.getRecipeManager();
                int slot;
                ItemStack stack;
                Optional<RecipeHolder<CleaningRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.CLEANING_TYPE.get(),
                        new CleaningRecipeInput(player.getMainHandItem()),
                        level);
                if (recipeOptionalMain.isPresent()) {
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
                if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                    return;
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), hand, Animation.CLEANING));
                }
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


    public static void SticksTongsGetterProcedure(Level level, Player player) {
        if (ConditionsHelper.isMetSticksTongsGetterConditions(player, level)) {
            if (ConditionsHelper.isMetMicsConditions(player)) {
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

                if (recipeOptionalTongsMain.isPresent()) {
                    hand = InteractionHand.MAIN_HAND;
                    inventory.add(recipeOptionalTongsMain.get().value().assemble(new TongsGetterRecipeInput(player.getMainHandItem()), level.registryAccess()));
                    inventory.setItem(inventory.selected, ModItems.TONGS.toStack());
                } else if (recipeOptionalTongsOff.isPresent()) {
                    inventory.add(recipeOptionalTongsOff.get().value().assemble(new TongsGetterRecipeInput(player.getOffhandItem()), level.registryAccess()));
                    inventory.setItem(40, ModItems.TONGS.toStack());
                }
                AnimationHelper.playSticksTongsGettingAnimation(hand);
            }
        }

    }

    public static void GrindingProcedure(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (ConditionsHelper.isMetGrindingConditions(player, level, pos)) {
            if (ConditionsHelper.isMetMicsConditions(player) && player.isShiftKeyDown()) {
                InteractionHand hand = InteractionHand.OFF_HAND;
                int slot = 40;
                ItemStack result;
                Optional<RecipeHolder<GrindRecipe>> recipeOptional;
                Inventory inventory = player.getInventory();
                RecipeManager recipeManager = level.getRecipeManager();
                int maxStage;
                int itemAmount = 0;
                int emptyslot = 0;

                Optional<RecipeHolder<GrindRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.GRIND_TYPE.get(),
                        new GrindRecipeInput(player.getMainHandItem()),
                        level);

                Optional<RecipeHolder<GrindRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.GRIND_TYPE.get(),
                        new GrindRecipeInput(player.getOffhandItem()),
                        level);

                if (recipeOptionalMain.isPresent()) {
                    if (player.getOffhandItem().isEmpty()){
                        emptyslot = 40;
                    }
                    recipeOptional = recipeOptionalMain;
                    hand = InteractionHand.MAIN_HAND;
                    slot = inventory.selected;
                    result = recipeOptionalMain.get().value().assemble(new GrindRecipeInput(player.getMainHandItem()), level.registryAccess());
                } else {
                    if (player.getMainHandItem().isEmpty()){
                        emptyslot = inventory.selected;
                    }
                    recipeOptional = recipeOptionalOff;
                    result = recipeOptionalOff.get().value().assemble(new GrindRecipeInput(player.getOffhandItem()), level.registryAccess());
                    hand = InteractionHand.OFF_HAND;
                }

                maxStage = recipeOptional.get().value().maxStage();

                ItemStack processingItem = inventory.getItem(slot);
                ItemStack singleProcessingItem;

                int currentStage = inventory.getItem(slot).getOrDefault(ModDataComponents.GRIND_STATE, 1);
                if (!player.getCooldowns().isOnCooldown(inventory.getItem(slot).getItem())) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), hand, Animation.GRINDING));
                    }

                    if ((processingItem.getCount() > 1)){
                        currentStage = processingItem.getOrDefault(ModDataComponents.GRIND_STATE, 1);
                        singleProcessingItem = processingItem.copyWithCount(1);
                        singleProcessingItem.set(ModDataComponents.GRIND_STATE, processingItem.getOrDefault(ModDataComponents.GRIND_STATE, 1) + 1);
                        int currentSingleStage = singleProcessingItem.getOrDefault(ModDataComponents.GRIND_STATE, 1);
                        if (currentSingleStage <= maxStage){
                            inventory.add(singleProcessingItem);
                            processingItem.shrink(1);
                            player.getCooldowns().addCooldown(processingItem.getItem(), 10);
                            Utils.playGrindingSound((ServerLevel) level, player);
                            Utils.sendGrindingParticles((ServerLevel) level, event.getPos(), inventory.getItem(slot));

                        } else {
                            processingItem.shrink(1);
                            inventory.add(result);
                            player.getCooldowns().addCooldown(processingItem.getItem(), 20);
                            Utils.playGrindingSound((ServerLevel) level, player);
                            Utils.sendGrindingParticles((ServerLevel) level, event.getPos(), inventory.getItem(slot));
                        }

                    } else {

                        event.setCanceled(true);
                        Utils.playGrindingSound((ServerLevel) level, player);
                        if (currentStage < maxStage) {
                            inventory.getItem(slot).set(ModDataComponents.GRIND_STATE, inventory.getItem(slot).getOrDefault(ModDataComponents.GRIND_STATE, 1) + 1);
                            player.getCooldowns().addCooldown(inventory.getItem(slot).getItem(), 10);
                            Utils.sendGrindingParticles((ServerLevel) level, event.getPos(), inventory.getItem(slot));
                        } else {
                            player.getCooldowns().addCooldown(inventory.getItem(slot).getItem(), 20);
                            Utils.sendGrindingParticles((ServerLevel) level, event.getPos(), inventory.getItem(slot));
                            inventory.getItem(slot).set(ModDataComponents.GRIND_STATE, 1);
                            inventory.removeItem(slot, 1);
                            inventory.add(result);
                        }
                    }


                } else {
                    event.setCanceled(true);
                }


            }
        } else if (ConditionsHelper.isGrindStone(event.getLevel().getBlockState(pos).getBlock()) && player.isShiftKeyDown()){
            event.setCanceled(true);
        }
    }

    public static void CuttingProcedure(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (ConditionsHelper.isMetCuttingConditions(player, level, pos)) {
            if (ConditionsHelper.isMetMicsConditions(player) && player.isShiftKeyDown()){
                InteractionHand hand = InteractionHand.OFF_HAND;
                int slot = 40;
                ItemStack result;
                Optional<RecipeHolder<CuttingRecipe>> recipeOptional;
                Inventory inventory = player.getInventory();
                RecipeManager recipeManager = level.getRecipeManager();

                Optional<RecipeHolder<CuttingRecipe>> recipeOptionalMain = recipeManager.getRecipeFor(
                        ModRecipes.CUTTING_TYPE.get(),
                        new CuttingRecipeInput(player.getMainHandItem()),
                        level);

                Optional<RecipeHolder<CuttingRecipe>> recipeOptionalOff = recipeManager.getRecipeFor(
                        ModRecipes.CUTTING_TYPE.get(),
                        new CuttingRecipeInput(player.getOffhandItem()),
                        level);

                if (recipeOptionalMain.isPresent()) {
                    recipeOptional = recipeOptionalMain;
                    hand = InteractionHand.MAIN_HAND;
                    slot = inventory.selected;
                    result = recipeOptionalMain.get().value().assemble(new CuttingRecipeInput(player.getMainHandItem()), level.registryAccess());
                } else {
                    recipeOptional = recipeOptionalOff;
                    result = recipeOptionalOff.get().value().assemble(new CuttingRecipeInput(player.getOffhandItem()), level.registryAccess());
                    hand = InteractionHand.OFF_HAND;
                }
                if (player.getCooldowns().isOnCooldown(inventory.getItem(slot).getItem())){
                    return;
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), hand, Animation.CUTTING));
                }
                event.setCanceled(true);
                Utils.playCuttingSound((ServerLevel) level, player);
                Utils.sendCuttingParticles((ServerLevel) level, pos, inventory.getItem(slot));
                player.getCooldowns().addCooldown(inventory.getItem(slot).getItem(), 5);
                inventory.getItem(slot).shrink(1);
                inventory.add(result);

            }
        }
    }


    public static void CarvingProcedureTick(PlayerTickEvent.Post event){

        Player player = event.getEntity();
        Level level = player.level();

        if (level == null) {
            RealisticForging.LOGGER.error("LEVEL IS NULL, WHY");
            return;
        }
        if (level.isClientSide && Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK){
            BlockHitResult blockHit = (BlockHitResult) Minecraft.getInstance().hitResult;
            Block targetBlock = level.getBlockState(blockHit.getBlockPos()).getBlock();
            if (ConditionsHelper.isHoldingCarvingHammer(player) && ConditionsHelper.isHoldingChisel(player)){
                InteractionHand hand = null;
                if (player.getMainHandItem().is(ModItems.POINTCHISEL)){
                    hand = InteractionHand.MAIN_HAND;
                } else if (player.getOffhandItem().is(ModItems.POINTCHISEL)){
                    hand = InteractionHand.OFF_HAND;
                }
                if ((Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK && targetBlock == Blocks.DIAMOND_ORE)){
                    if (!CARVING_ACTION){
                        AnimationHelper.playChiselingAnimation(hand);
                        RealisticForging.LOGGER.debug("PLAYING CHISELING ANIMATION");
                        CARVING_ACTION = true;
                        RealisticForging.LOGGER.debug("MADE CARVING ACTION TRUE");
                    }

                } else {
                    if (CARVING_ACTION){
                        AnimationHelper.cancelAnimation(player);
                        CARVING_ACTION = false;
                        targetBlock = null;
                        RealisticForging.LOGGER.debug("MADE CARVING ACTION FALSE");
                    }

                }

            }
        }


    }

    public static void CarvingProcedureHit(PlayerInteractEvent.RightClickBlock event){
        if (CARVING_ACTION){
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), ConditionsHelper.getCarvingHammerHand(player), Animation.CHISELINGHIT));
            }
        }
    }
}

