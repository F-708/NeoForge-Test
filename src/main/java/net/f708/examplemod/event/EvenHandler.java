//package net.f708.examplemod.event;
//
//import net.f708.examplemod.item.ModItems;
//import net.minecraft.world.damagesource.DamageSource;
//import net.minecraft.world.damagesource.DamageSources;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.common.Mod;
//import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
//import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
//import net.neoforged.neoforge.event.entity.living.LivingEvent;
//import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//
//@EventBusSubscriber(modid = "examplemod")
//public class EvenHandler {
//    @SubscribeEvent
//    public static void onPlayerTick(PlayerTickEvent.Post event) {
//            Player entity = event.getEntity();
//        Inventory inventory = entity.getInventory();
//        if (inventory.contains(new ItemStack(ModItems.HOTRAWIRONORE.get()))) {
//            entity.hurt(entity.damageSources().onFire(), 2f);
//        }
//
//
//    }
//}
