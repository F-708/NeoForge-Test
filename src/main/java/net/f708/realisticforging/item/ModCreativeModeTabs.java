package net.f708.realisticforging.item;

import net.f708.realisticforging.RealisticForging;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealisticForging.MODID);

    public static final Supplier<CreativeModeTab> FORGING_TAB = CREATIVE_MODE_TAB.register("forging_tab",
            () -> CreativeModeTab.builder().icon(()-> new ItemStack(ModItems.TONGS.get()))
                    .title(Component.translatable("creativetab.realisticforging.forging_tab"))
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(itemRegistry ->
                                output.accept(itemRegistry.get())
                        );


                    })



                    .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TAB.register(bus);
    }
}
