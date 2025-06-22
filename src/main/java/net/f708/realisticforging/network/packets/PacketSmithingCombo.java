package net.f708.realisticforging.network.packets;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.data.ModData;
import net.f708.realisticforging.data.SmithingHammerComboData;
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
public record PacketSmithingCombo(SmithingHammerComboData data) implements CustomPacketPayload {


    public static final Type<PacketSmithingCombo> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "smithing_hammer_combo"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSmithingCombo> STREAM_CODEC = StreamCodec
            .of((RegistryFriendlyByteBuf buffer, PacketSmithingCombo packet) -> {
                buffer.writeNbt(packet.data().serializeNBT(buffer.registryAccess()));
            }, (RegistryFriendlyByteBuf buffer) -> {
                SmithingHammerComboData data = new SmithingHammerComboData();
                data.deserializeNBT(buffer.registryAccess(), Objects.requireNonNull(buffer.readNbt()));
                return new PacketSmithingCombo(data);
            });

    public static void handleData(final PacketSmithingCombo message, final IPayloadContext context) {
        if (context.flow().isServerbound() && message.data() != null) {
            context.enqueueWork(() -> {

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
    private static void handleClientData(PacketSmithingCombo message) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            var smithingData = player.getData(ModData.SMITHING_HAMMER_COMBO);
            smithingData.deserializeNBT(player.registryAccess(), message.data().serializeNBT(player.registryAccess()));
            player.setData(ModData.SMITHING_HAMMER_COMBO, smithingData);
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketSmithingCombo.TYPE,
                PacketSmithingCombo.STREAM_CODEC,
                PacketSmithingCombo::handleData
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
