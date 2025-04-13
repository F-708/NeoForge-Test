package net.f708.examplemod.utils;

import net.f708.examplemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import java.util.Random;

public class TongsPickup {
    public TongsPickup(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.FURNACE)
                ||
                (event.getLevel().getBlockState(event.getPos()).is(Blocks.BLAST_FURNACE))) {
            Player player = event.getEntity();
            BlockPos pos = event.getPos();
            Level level = event.getLevel();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
                ItemStack MainHeldItem = event.getItemStack();
                ItemStack OffHeldItem = player.getOffhandItem();
                if (MainHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)
                ||
                OffHeldItem.is(ModItems.TONGS) && furnace.getItem(2).is(ModItems.HOTRAWIRONORE)) {


                    Inventory inventory = player.getInventory();
                    int slot = inventory.selected;
                    event.setCanceled(true);
                        inventory.setItem(slot, new ItemStack(ModItems.PICKEDHOTRAWIRON.get()));
                        furnace.setItem(2, ItemStack.EMPTY);
                        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) * 0.5F);
                        Random random = new Random();
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, random.nextInt(3), 0, 0.1, 0, 0.05);


                    }
            }
        }
    }
}
