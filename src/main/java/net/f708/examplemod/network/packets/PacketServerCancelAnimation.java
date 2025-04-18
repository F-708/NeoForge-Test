package net.f708.examplemod.network.packets;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.network.NetworkHandler;
import net.f708.examplemod.utils.animations.AnimationHelper;
import net.f708.examplemod.utils.animations.PlayerAnimator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public record PacketServerCancelAnimation() implements CustomPacketPayload {

    public static final Type<PacketServerCancelAnimation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "cancel_server_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketServerCancelAnimation> STREAM_CODEC =
            StreamCodec.of((RegistryFriendlyByteBuf buffer, PacketServerCancelAnimation message) -> {
            }, (RegistryFriendlyByteBuf buffer) -> new PacketServerCancelAnimation());

    public static void handleData(final PacketServerCancelAnimation message, final IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (!context.player().level().isClientSide()) {
                    PlayerAnimator.cancelAnimation(context.player().level(), context.player());                }
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });

        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketServerCancelAnimation.TYPE,
                PacketServerCancelAnimation.STREAM_CODEC,
                PacketServerCancelAnimation::handleData
        );
    }

    @Override
    public @NotNull Type<PacketServerCancelAnimation> type() {
        return TYPE;
    }

}