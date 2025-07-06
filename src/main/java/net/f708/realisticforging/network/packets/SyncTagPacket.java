
package net.f708.realisticforging.network.packets;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.network.NetworkHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// Packet Please Play Player Animation
@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD)
public record SyncTagPacket (Integer entityId) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<SyncTagPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "sync_tag_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTagPacket> STREAM_CODEC =
            StreamCodec.of((RegistryFriendlyByteBuf buffer, SyncTagPacket message) -> {
                buffer.writeInt(message.entityId());
            }, (RegistryFriendlyByteBuf buffer) -> new SyncTagPacket(buffer.readInt()));

    public static void handleData(final SyncTagPacket message, final IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleClientData(message)).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientData(SyncTagPacket message) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        Camera camera = new Camera();
    }


    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event){
        NetworkHandler.addNetworkMessage(
                SyncTagPacket.TYPE,
                SyncTagPacket.STREAM_CODEC,
                SyncTagPacket::handleData
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}

