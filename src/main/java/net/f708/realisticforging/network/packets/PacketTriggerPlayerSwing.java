package net.f708.realisticforging.network.packets;

import net.f708.realisticforging.RealisticForging;
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
public record PacketTriggerPlayerSwing (IsSwingingData data) implements CustomPacketPayload {


    public static final Type<PacketTriggerPlayerSwing> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_parry_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTriggerPlayerSwing> STREAM_CODEC = StreamCodec
            .of((RegistryFriendlyByteBuf buffer, PacketTriggerPlayerSwing packet) -> {
                buffer.writeNbt(packet.data().serializeNBT(buffer.registryAccess()));
            }, (RegistryFriendlyByteBuf buffer) -> {
                IsSwingingData data = new IsSwingingData();
                data.deserializeNBT(buffer.registryAccess(), Objects.requireNonNull(buffer.readNbt()));
                return new PacketTriggerPlayerSwing(data);
            });

    public static void handleData(final PacketTriggerPlayerSwing message, final IPayloadContext context) {
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
    private static void handleClientData(PacketTriggerPlayerSwing message) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            var swingingData = player.getData(ModData.IS_SWINGING);
            swingingData.deserializeNBT(player.registryAccess(), message.data().serializeNBT(player.registryAccess()));
            player.setData(ModData.IS_SWINGING, swingingData);
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketTriggerPlayerSwing.TYPE,
                PacketTriggerPlayerSwing.STREAM_CODEC,
                PacketTriggerPlayerSwing::handleData
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
