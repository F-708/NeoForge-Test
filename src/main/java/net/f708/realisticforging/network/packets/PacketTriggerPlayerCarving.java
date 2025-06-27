package net.f708.realisticforging.network.packets;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.data.IsCarvingData;
import net.f708.realisticforging.data.IsSwingingData;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD)
public record PacketTriggerPlayerCarving(IsCarvingData data) implements CustomPacketPayload {


    public static final Type<PacketTriggerPlayerCarving> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "is_carving"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTriggerPlayerCarving> STREAM_CODEC = StreamCodec
            .of((RegistryFriendlyByteBuf buffer, PacketTriggerPlayerCarving packet) -> {
                buffer.writeNbt(packet.data().serializeNBT(buffer.registryAccess()));
            }, (RegistryFriendlyByteBuf buffer) -> {
                IsCarvingData data = new IsCarvingData();
                data.deserializeNBT(buffer.registryAccess(), Objects.requireNonNull(buffer.readNbt()));
                return new PacketTriggerPlayerCarving(data);
            });

    public static void handleData(final PacketTriggerPlayerCarving message, final IPayloadContext context) {
        if (context.flow().isServerbound() && message.data() != null) {
            context.enqueueWork(() -> {

                /// /// /// /// /// /// /// ///
            ///  /// /// /// // // / / / / //

            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
        if (context.flow().isClientbound() && message.data() != null) {
            context.enqueueWork(() -> handleClientData(message)).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientData(PacketTriggerPlayerCarving message) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            var carvingData = player.getData(ModData.IS_CARVING);
            carvingData.deserializeNBT(player.registryAccess(), message.data().serializeNBT(player.registryAccess()));
            player.setData(ModData.IS_CARVING, carvingData);
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketTriggerPlayerCarving.TYPE,
                PacketTriggerPlayerCarving.STREAM_CODEC,
                PacketTriggerPlayerCarving::handleData
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
