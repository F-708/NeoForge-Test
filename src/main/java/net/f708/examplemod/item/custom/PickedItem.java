package net.f708.examplemod.item.custom;

import net.f708.examplemod.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


@EventBusSubscriber(modid = "examplemod")
public class PickedItem extends Item {
    public PickedItem(Properties properties) {
        super(properties);
    }

@SubscribeEvent
    public static void test (PlayerInteractEvent.RightClickBlock event) {}
//    @SubscribeEvent
//    public static void anvil_smith(PlayerInteractEvent.RightClickBlock event) {
//        Player player = event.getEntity();
//        ItemStack mainHand = player.getMainHandItem();
//        ItemStack offHand = player.getOffhandItem();
//        Level level = event.getLevel();
//        BlockPos pos = event.getPos();
//        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
//        if (block instanceof AnvilBlock){
//            if (mainHand.is(ModItems.SMITHINGHAMMER) && offHand.getItem() instanceof PickedItem) {
//                mainHand.update(
//                        BASIC_EXAMPLE.get(),
//                        new ForgingRecord("ore", 0),
//                        record -> new ForgingRecord("ore", record.stage() + 1)
//                );
//            }
//        }
//
//    }


    //    @Override
//    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//        if (entity instanceof Player player) {
//            Inventory inventory = player.getInventory();
//            if (!level.isClientSide()) {
//                if (inventory.contains(new ItemStack(stack.getItem()))) {
//                    int amount = inventory.countItem(stack.getItem());
//                    if (amount > 2) {
//                        player.hurt(player.damageSources().onFire(), 1f);
//                    }
//
//                }
//            }
//        }
//    }


}
