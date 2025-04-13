package net.f708.examplemod.event;

import net.f708.examplemod.attributes.ModAttributes;
import net.f708.examplemod.item.ModItems;
import net.f708.examplemod.modProcedures.CleaningProcedure;
import net.f708.examplemod.modProcedures.ForgingProcedure;
import net.f708.examplemod.modProcedures.HotItemsProcedure;
import net.f708.examplemod.modProcedures.PickingProcedure;
import net.f708.examplemod.utils.*;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Objects;

@EventBusSubscriber(modid = "examplemod")
public class EventHandler {


    @SubscribeEvent
    public static void getHotItemFromFurnace(PlayerInteractEvent.RightClickBlock event) {
        PickingProcedure.pickHotItemTongs(event);
    }

    @SubscribeEvent
    public static void hotItemsInInventory(PlayerTickEvent.Post event) {
        HotItemsProcedure hotItemsDealDamage = new HotItemsProcedure(event);
    }

    @SubscribeEvent
    public static void cleaningItem(PlayerInteractEvent.RightClickItem event) {
        CleaningProcedure.cleanItem(event);
    }

    @SubscribeEvent
    public static void useWithHammer(PlayerInteractEvent.RightClickBlock event) {
        ForgingProcedure.accept(event);
    }

}

    //    @SubscribeEvent
//    public static void useOnAnvil(PlayerInteractEvent.RightClickBlock event){
//        ForgingProcess.useOnAnvil(event);
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
//                TickScheduler.schedule(() -> {ForgingProcess.useOnAnvil(event);}, 8);
//                event.setCanceled(true);
//
//            }
//
//        }
//
//    }
//}


    











