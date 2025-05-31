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
import net.f708.realisticforging.utils.Animation;
import net.f708.realisticforging.utils.animations.AnimationHelper;
import net.f708.realisticforging.utils.animations.PlayerAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

// Packet Please Play Player Animation
@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD)
public record PacketPPPAnimation (Integer entityId, Animation animation, Boolean RH, int fadeInTicks) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<PacketPPPAnimation> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "ppp_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPPPAnimation> STREAM_CODEC =
            StreamCodec.of((RegistryFriendlyByteBuf buffer, PacketPPPAnimation message) -> {
                buffer.writeInt(message.entityId);
                buffer.writeEnum(message.animation);
                buffer.writeBoolean(message.RH);
                buffer.writeInt(message.fadeInTicks);
            }, (RegistryFriendlyByteBuf buffer) -> new PacketPPPAnimation(buffer.readInt(), buffer.readEnum(Animation.class), buffer.readBoolean(), buffer.readInt()));

    public static void handleData(final PacketPPPAnimation message, final IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleClientData(message)).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientData(PacketPPPAnimation message) {
        Level level = Minecraft.getInstance().level;
        if (Minecraft.getInstance().player == null || level == null) return;
        if (level.getEntity(message.entityId()) != null) {
            Player player = (Player) level.getEntity(message.entityId());
            if (player instanceof AbstractClientPlayer clientPlayer) {
                switch (message.animation){
                    case CANCEL -> AnimationHelper.cancelAnimation(clientPlayer);
                    case FORGING -> AnimationHelper.playForgingAnimation(message.RH, message.fadeInTicks);
                    case COOLING -> AnimationHelper.playCoolingAnimation(message.RH, message.fadeInTicks);
                    case PICKING, GRINDING -> AnimationHelper.playSwingAnimation(message.RH);
                    case CUTTING -> AnimationHelper.playCuttingAnimation(message.RH, message.fadeInTicks);
                    case CLEANING -> AnimationHelper.playCleaningAnimationBareHands(message.RH, message.fadeInTicks);
                    case CARVING -> AnimationHelper.playCarvingAnimation(message.RH, message.fadeInTicks);
                    case SLEDGEHAMMERSWING -> AnimationHelper.playSledgeHammerAnimation(message.RH, message.fadeInTicks);
                    case SLEDGEHAMMERSWINGSECOND -> AnimationHelper.playSledgeHammerAnimationCombo(message.RH, message.fadeInTicks);
                    default -> {
                        return;
                    }

                }
                }
            }
        }


    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event){
        NetworkHandler.addNetworkMessage(
                PacketPPPAnimation.TYPE,
                PacketPPPAnimation.STREAM_CODEC,
                PacketPPPAnimation::handleData
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
