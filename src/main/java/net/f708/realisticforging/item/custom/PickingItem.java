package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.component.ItemStackRecord;
import net.f708.realisticforging.network.packets.PacketPPPAnimation;
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.ConditionsHelper;
import net.f708.realisticforging.utils.ModTags;
import net.f708.realisticforging.utils.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class PickingItem extends Item {
    public PickingItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        Player player = (Player) entity;
        if (PickingItem.isHoldingTongs(player)) {
            if (player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                return;
            }
            ItemStack tongs = switch (getHandWithTongs(player)){
                case MAIN_HAND -> player.getMainHandItem();
                case OFF_HAND -> player.getOffhandItem();
            };
            if (isTongsAreFree(tongs)) {
                    if (player.getCooldowns().isOnCooldown(this)) {
                        return;
                    }
                    Optional<ItemStack> hotItem = player.getInventory().items.stream()
                            .filter(itemStack -> itemStack.is(ModTags.Items.VERY_HOT_ITEM))
                            .findFirst();

                    if (hotItem.isPresent()) {
                        ItemStack hotStack = hotItem.get();
                        ItemStackRecord.setItemStackIntoDataComponent(hotStack, tongs);
                        hotStack.shrink(1);
                        player.getCooldowns().addCooldown(this, 10);
                        player.swing(getHandWithTongs(player));
                    }
            }
        }

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack tongs = context.getItemInHand();
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }
        if (level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace ||
        level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            if (isTongsAreFree(tongs)){
                getItemFromContainer(pos, level, player, tongs);
            } else {
                setItemIntoContainer(pos, level, player, tongs);
                if (level.getBlockState(pos).getBlock() instanceof CauldronBlock){
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (ConditionsHelper.isHoldingHammer(player)) {
            return InteractionResult.PASS;
        }

        else {
            if (ConditionsHelper.isWaterCauldron(level.getBlockState(pos).getBlock()) || ConditionsHelper.isPlayerInWater(player)) {
                return InteractionResult.PASS;
            } else {
                use(level, player, context.getHand());
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.SUCCESS;

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        ItemStack handWithTongs = player.getItemInHand(usedHand);
        ItemStack handWithItem = (usedHand == InteractionHand.MAIN_HAND)
                ? player.getOffhandItem()
                : player.getMainHandItem();

        if (ConditionsHelper.isHoldingHammer(player)) {
            return handleHammerUsage(handWithTongs, player, usedHand);
        }

        if (isItemPickable(handWithItem, player, level)) {
            return handleItemPickup(handWithItem, handWithTongs, player, usedHand);
        }

        return handleEmptyTongs(handWithTongs, player, usedHand);
    }

//    @Override
//    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
//        boolean flag = remainingUseDuration % 20 == 0;
//        if (flag) {
//            Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
//            boolean RH = switch (PickingItem.getHandWithTongs(player)){
//                case MAIN_HAND -> true;
//                case OFF_HAND -> false;
//                case null, default -> false;
//            };
//            if (player instanceof ServerPlayer serverPlayer){
//                PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGHELPERHOLDING, RH, 0));
//            }
//        }
//    }

//    @Override
//    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
//        Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
//        boolean RH = switch (PickingItem.getHandWithTongs(player)){
//            case MAIN_HAND -> true;
//            case OFF_HAND -> false;
//            case null, default -> false;
//        };
//        if (player instanceof ServerPlayer serverPlayer){
//            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.FORGINGHELPERDOWNSWING, RH, 5));
//        }
//        player.stopUsingItem();
//    }

    private InteractionResultHolder<ItemStack> handleHammerUsage(ItemStack tongs, Player player, InteractionHand hand) {
        if (!isTongsAreFree(tongs)) {
            ItemStack stack = ItemStackRecord.getStackFromDataComponent(tongs);
            if (!stack.is(ModTags.Items.PICKABLE_WITH_TONGS)) {
                player.addItem(stack);
                ItemStackRecord.clearItemStackFromDataComponent(tongs);
                player.getCooldowns().addCooldown(this, 10);
                player.swing(hand);
                if (stack.is(ModTags.Items.VERY_HOT_ITEM)){
                    tongs.hurtAndBreak(1, player, tongs.getEquipmentSlot());
                }
            }
        }
        return InteractionResultHolder.fail(tongs);
    }

    private InteractionResultHolder<ItemStack> handleItemPickup(ItemStack item, ItemStack tongs, Player player, InteractionHand hand) {
        if (isTongsAreFree(tongs)) {
            if (item.getItem() instanceof PickingItem){
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
            ItemStackRecord.setItemStackIntoDataComponent(item, tongs);
            item.shrink(1);
            player.getCooldowns().addCooldown(this, 10);
            player.swing(hand);
        } else {
            if (veryHotItemInTongs(tongs, player, hand)) return InteractionResultHolder.fail(tongs);
        }
        return InteractionResultHolder.fail(tongs);
    }

    private InteractionResultHolder<ItemStack> handleEmptyTongs(ItemStack tongs, Player player, InteractionHand hand) {
        if (isTongsAreFree(tongs)) {
            return InteractionResultHolder.pass(tongs);
        } else {
            if (veryHotItemInTongs(tongs, player, hand)) return InteractionResultHolder.fail(tongs);
            return InteractionResultHolder.fail(tongs);
        }
    }

    private boolean veryHotItemInTongs(ItemStack tongs, Player player, InteractionHand hand) {
        if (ItemStackRecord.getStackFromDataComponent(tongs).is(ModTags.Items.VERY_HOT_ITEM)) {
            return true;
        }
        player.addItem(ItemStackRecord.getStackFromDataComponent(tongs));
        ItemStackRecord.clearItemStackFromDataComponent(tongs);
        player.getCooldowns().addCooldown(this, 10);
        player.swing(hand);
        return false;
    }

    public void startHolding(Player player, Level level, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }
        boolean RH = switch (hand){
            case MAIN_HAND -> true;
            case OFF_HAND -> false;
            default -> false;
        };
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new PacketPPPAnimation(player.getId(), Animation.PICKING, RH, 2));
        }
        TickScheduler.schedule(()->{
            player.startUsingItem(hand);
        }, 6);
    }





    public static boolean isTongsAreFree(ItemStack tongs){
        return ItemStackRecord.getStackFromDataComponent(tongs).isEmpty();
    }

    public static boolean isHoldingTongs(Player player){
        return (player.getMainHandItem().getItem() instanceof PickingItem) || (player.getOffhandItem().getItem() instanceof PickingItem);
    }

    public static boolean isItemPickable(ItemStack stack, Player player, Level level){
        return stack.is(ModTags.Items.PICKABLE_WITH_TONGS) || ConditionsHelper.isHoldingForgeableItem(player,level);
    }

    @Override
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return super.isNotReplaceableByPickAction(stack, player, inventorySlot);
    }

    public static InteractionHand getHandWithTongs(Player player){
        boolean result = false;
        InteractionHand hand;
        if (player.getMainHandItem().getItem() instanceof PickingItem) {
            hand = InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof PickingItem) {
            hand = InteractionHand.OFF_HAND;
        } else {
            hand = null;
        }
        return hand;
    }

    public static void getItemFromContainer(BlockPos pos, Level level, Player player, ItemStack tongs){
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof AbstractFurnaceBlockEntity furnaceBlock){
            if (isItemPickable(furnaceBlock.getItem(2), player, level)) {
                ItemStackRecord.setItemStackIntoDataComponent(furnaceBlock.getItem(2), tongs);
                furnaceBlock.getItem(2).shrink(1);
            }
        } else if (blockEntity instanceof ChestBlockEntity chestBlock && isTongsAreFree(tongs)) {
            for (int i = 0; i < chestBlock.getContainerSize(); i++) {
                if(chestBlock.canPlaceItem(i, ItemStackRecord.getStackFromDataComponent(tongs))){
                    if (chestBlock.hasAnyMatching(stack -> stack.getItem().builtInRegistryHolder().is(ModTags.Items.PICKABLE_WITH_TONGS))) {
                        ItemStack stack = chestBlock.getItem(i);
                        if (!stack.isEmpty() && stack.getItem().builtInRegistryHolder().is(ModTags.Items.PICKABLE_WITH_TONGS)) {
                            ItemStackRecord.setItemStackIntoDataComponent(stack, tongs);
                            chestBlock.removeItem(i, 1);
                            chestBlock.setChanged();
                            break;
                        }
                    }
                }
            }


        }
    }

    public static void setItemIntoContainer(BlockPos pos, Level level, Player player, ItemStack tongs){
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof AbstractFurnaceBlockEntity furnaceBlock) {


            ItemStack tongsItem = switch (getHandWithTongs(player)) {
                case MAIN_HAND -> player.getMainHandItem();
                case OFF_HAND -> player.getOffhandItem();
            };

            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(
                    RecipeType.SMELTING,
                    new SingleRecipeInput(ItemStackRecord.getStackFromDataComponent(tongsItem)),
                    level
            );

            if (recipe.isPresent()) {
                furnaceBlock.setItem(0, ItemStackRecord.getStackFromDataComponent(tongsItem));
                ItemStackRecord.clearItemStackFromDataComponent(tongsItem);
            }

        }
    }

    public static boolean isHelperActive(Player player){
        return player.getTags().contains("HELPER_ACTIVE");
    }
    public static void removeHelperActive(Player player){
        player.getTags().remove("HELPER_ACTIVE");
    }
    public static void setHelperActive(Player player){
        player.getTags().add("HELPER_ACTIVE");
    }

}
