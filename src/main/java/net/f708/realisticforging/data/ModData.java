package net.f708.realisticforging.data;

import net.f708.realisticforging.RealisticForging;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModData {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RealisticForging.MODID);

    public static final Supplier<AttachmentType<IsSwingingData>> IS_SWINGING = ATTACHMENT_TYPES.register("is_swinging", () ->
            AttachmentType.serializable(IsSwingingData::new).build());



}
