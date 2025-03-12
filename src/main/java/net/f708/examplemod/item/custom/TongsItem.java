package net.f708.examplemod.item.custom;

import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

public class TongsItem extends Item {
    public TongsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.getBlockState(blockpos).is(Blocks.FURNACE)){
            return super.useOn(context);
        }
        else {

            Player player = context.getPlayer();
            ItemStack itemstack = context.getItemInHand();
            AbstractFurnaceBlockEntity furnaceBlockEntity = (AbstractFurnaceBlockEntity) level.getBlockEntity(blockpos);
            if(!furnaceBlockEntity.getItem(2).isEmpty()) {
                player.hurt(player.damageSources().onFire(), 3f);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }

}
