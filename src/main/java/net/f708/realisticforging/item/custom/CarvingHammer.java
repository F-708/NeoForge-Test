package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.utils.ConditionsHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;

public class CarvingHammer extends Item {
    public CarvingHammer(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        Level level = context.getLevel();
//        Player player = context.getPlayer();
//        InteractionHand hand = context.getHand();
//        Block block = level.getBlockState(context.getClickedPos()).getBlock();
//
//        if (ConditionsHelper.isMetCarvingConditions())
//    }


}
