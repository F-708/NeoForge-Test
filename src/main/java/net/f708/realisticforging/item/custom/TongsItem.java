package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.TEST.ItemInTongs;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TongsItem extends Item {
    public TongsItem(Properties properties) {
        super(properties);
    }
    ItemCooldowns cooldowns = new ItemCooldowns();

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack handWithTongs = player.getItemInHand(usedHand);
        ItemStack handWithItem = (usedHand == InteractionHand.MAIN_HAND)
                ? player.getOffhandItem()
                : player.getMainHandItem();

        if (!handWithItem.isEmpty() && !(handWithItem.getItem() instanceof BlockItem)) {
            ItemInTongs itemInTongs = handWithTongs.get(ModDataComponents.ITEM_IN_TONGS);
            if (itemInTongs == null || itemInTongs.stack().isEmpty()) {
                // Проверка, что handWithItem не пустой и количество > 0
                if (!handWithItem.isEmpty() && handWithItem.getCount() > 0) {
                    handWithTongs.set(ModDataComponents.ITEM_IN_TONGS, new ItemInTongs(handWithItem));
                    handWithItem.shrink(1);
                }
            }
        } else {
            ItemInTongs itemInTongs = handWithTongs.get(ModDataComponents.ITEM_IN_TONGS);
            if (itemInTongs != null && !itemInTongs.stack().isEmpty()) {
                // Убедитесь, что предмет не пустой перед добавлением в инвентарь
                if (!itemInTongs.stack().isEmpty()) {
                    player.getInventory().add(itemInTongs.stack());
                }
                handWithTongs.remove(ModDataComponents.ITEM_IN_TONGS); // Удаление компонента
            }
        }

        return InteractionResultHolder.success(handWithTongs);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            if (isSelected) {
                if (player.getInventory().contains(new ItemStack(ModItems.HOTRAWIRONORE.get()))) {
                    Inventory inventory = player.getInventory();
                    int slot = inventory.selected;
                    inventory.setItem(slot, new ItemStack(ModItems.TONGSHOTIRON.get()));
                    ItemStack item = inventory.getItem(slot);
                    int ore = inventory.findSlotMatchingItem(new ItemStack(ModItems.HOTRAWIRONORE.get()));
                    inventory.setItem(ore, new ItemStack(Items.AIR));
                }
            }
        }
    }
}
