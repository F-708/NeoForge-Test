package net.f708.realisticforging.network.packets;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD)
public record PacketPlayAnimationAtPlayer(String animationName, Integer entityId, boolean override) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketPlayAnimationAtPlayer> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "sync_clients_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPlayAnimationAtPlayer> STREAM_CODEC =
            StreamCodec.of((RegistryFriendlyByteBuf buffer, PacketPlayAnimationAtPlayer message) -> {
                buffer.writeUtf(message.animationName);
                buffer.writeInt(message.entityId);
                buffer.writeBoolean(message.override);
            }, (RegistryFriendlyByteBuf buffer) -> new PacketPlayAnimationAtPlayer(buffer.readUtf(), buffer.readInt(), buffer.readBoolean()));

    public static void handleData(final PacketPlayAnimationAtPlayer message, final IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleClientData(message)).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientData(PacketPlayAnimationAtPlayer message) {
        Level level = Minecraft.getInstance().level;
        if (Minecraft.getInstance().player == null || level == null) return;
        if (level.getEntity(message.entityId()) != null) {
            Player player = (Player) level.getEntity(message.entityId());
            if (player == Minecraft.getInstance().player) return;
            if (player instanceof AbstractClientPlayer clientPlayer) {
                Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ResourceLocation
                        .fromNamespaceAndPath(RealisticForging.MODID, "player_animations"));
                if (associatedData instanceof ModifierLayer<?> modifierLayer) {
                    @SuppressWarnings("unchecked")
                    var animation = (ModifierLayer<IAnimation>) modifierLayer;
                    animation.replaceAnimationWithFade(
                            AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath
                                            (RealisticForging.MODID, message.animationName())))
                                    .playAnimation()
                                    .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                                    .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(true)));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        NetworkHandler.addNetworkMessage(
                PacketPlayAnimationAtPlayer.TYPE,
                PacketPlayAnimationAtPlayer.STREAM_CODEC,
                PacketPlayAnimationAtPlayer::handleData
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}