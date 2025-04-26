package net.f708.realisticforging.item.custom;

import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.utils.Utils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class PickedItem extends Item {
    public PickedItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player){
            AttributeMap attributeMap = player.getAttributes();
            if (player.getMainHandItem().getItem() instanceof PickedItem || player.getOffhandItem().getItem() instanceof PickedItem){
                Utils.descreaseInteractionRange(attributeMap, player);
            }else {
                Utils.returnInteractionRange(attributeMap, player);
            }

        }
    }
}
