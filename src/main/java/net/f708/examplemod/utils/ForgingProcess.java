package net.f708.examplemod.utils;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.f708.examplemod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

import static net.f708.examplemod.utils.ItemProcesses.FORGING_MAP;

public class ForgingProcess {



    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event) {
        Random random = new Random();
        Player player = event.getEntity();
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        Item mainHandItem = player.getMainHandItem().getItem();
        Item offHandItem = player.getOffhandItem().getItem();
        Level level = event.getLevel();
        Block block = level.getBlockState(event.getPos()).getBlock();
        BlockPos pos = event.getPos();
        if (block instanceof AnvilBlock) {
            if (FORGING_MAP.containsKey(offHandItem) && mainHandItem == ModItems.SMITHINGHAMMER.get()
                    ||
                    FORGING_MAP.containsKey(mainHandItem) && offHandItem == ModItems.SMITHINGHAMMER.get()
            ) {
                if (player.isShiftKeyDown()) {

                } else {
                    if (!level.isClientSide) {
                        Inventory inventory = player.getInventory();
                        ItemStack targetItem = FORGING_MAP.containsKey(mainHandItem) ? mainHand : offHand;
                        int slot = FORGING_MAP.containsKey(mainHandItem)
                                ? inventory.findSlotMatchingItem(player.getMainHandItem())
                                : 40;

                        Item resultItem = FORGING_MAP.get(targetItem.getItem());
                        if (resultItem != null) {
                            event.setCanceled(true);

                            inventory.setItem(slot, resultItem.asItem().getDefaultInstance());


                            ((ServerLevel) level).sendParticles(
                                    ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, random.nextInt(3, 5), 0, 0.1, 0, 0.05);
                            ((ServerLevel) level).sendParticles(
                                    ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, random.nextInt(3), 0, 0.1, 0, 0.05);
                            {

                            }
                        }
                    }


                }
            }
        }
    }
}
