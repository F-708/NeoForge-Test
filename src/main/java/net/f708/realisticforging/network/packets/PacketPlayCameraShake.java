package net.f708.realisticforging.network.packets;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.network.NetworkHandler;
import net.f708.realisticforging.utils.CameraUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD)
public record PacketPlayCameraShake(int duration, float intensity, int waves, float decay, boolean RH) implements CustomPacketPayload {

    public static final Type<PacketPlayCameraShake> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_camera_shake"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPlayCameraShake> STREAM_CODEC =
            StreamCodec.of((RegistryFriendlyByteBuf buffer, PacketPlayCameraShake message) -> {
                buffer.writeInt(message.duration);
                buffer.writeFloat(message.intensity);
                buffer.writeInt(message.waves);
                buffer.writeFloat(message.decay);
                buffer.writeBoolean(message.RH);
            }, (RegistryFriendlyByteBuf buffer) -> new PacketPlayCameraShake(buffer.readInt(), buffer.readFloat(), buffer.readInt(), buffer.readFloat(), buffer.readBoolean()));

    public static void handleData(final PacketPlayCameraShake message, final IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleClientData(message)).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientData(PacketPlayCameraShake message) {
        CameraUtils.triggerCameraShake(message.duration, message.intensity, message.waves, message.decay, message.RH);
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketPlayCameraShake.TYPE,
                PacketPlayCameraShake.STREAM_CODEC,
                PacketPlayCameraShake::handleData
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}