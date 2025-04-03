package net.f708.examplemod.item.custom;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


//@EventBusSubscriber(modid = "examplemod")
public class PickedItem extends Item {
    public PickedItem(Properties properties) {
        super(properties);
    }




}
