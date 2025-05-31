package net.f708.realisticforging.sounds;

import net.f708.realisticforging.RealisticForging;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, RealisticForging.MODID);

    public static final Supplier<SoundEvent> FORGING_SOUND = registerSoundEvent("forging");
    public static final Supplier<SoundEvent> CARVING_SOUND = registerSoundEvent("carving");
    public static final Supplier<SoundEvent> SMASHING_SOUND = registerSoundEvent("smashing");
    public static final Supplier<SoundEvent> SMASHING_BLOCK_SOUND = registerSoundEvent("smashing_block");

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
