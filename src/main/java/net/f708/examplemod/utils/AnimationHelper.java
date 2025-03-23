package net.f708.examplemod.utils;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.f708.examplemod.ExampleMod;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AnimationHelper {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        ExampleMod.LOGGER.info("Registering animation factory");
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "player_animations"),
                42,
                AnimationHelper::registerPlayerAnimation);
    }

    public static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

    public static void playAnimation(LevelAccessor world, Entity entity, String animationName) {
        try {
            if (world.isClientSide()) {
                playClientAnimation(entity, animationName);
            } else {
//                playServerAnimation(world, entity, animationName);
            }
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Error in PlayerAnimator::playAnimation: {}", e.getMessage(), e);
        }
    }

    private static void playClientAnimation(Entity entity, String animationName) {
        if (entity instanceof AbstractClientPlayer) {
            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
                    .get(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "player_animations"));
            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
                @SuppressWarnings("unchecked")
                var animation = (ModifierLayer<IAnimation>) modifierLayer;
                if (!animation.isActive()) {
                    animation.replaceAnimationWithFade(
                            AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(
                                            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, animationName)))
                                    .playAnimation()
                                    .setFirstPersonMode(getFirstPersonAlternatives())
                                    .setFirstPersonConfiguration(new FirstPersonConfiguration()
                                            .setShowRightArm(false)
                                            .setShowLeftItem(true)));
                }
            }
        }
    }


    private static FirstPersonMode getFirstPersonAlternatives() {
        if (ModList.get().isLoaded("firstperson")) {
            return FirstPersonMode.DISABLED;
        } else {
            return FirstPersonMode.THIRD_PERSON_MODEL;
        }
    }

//    @OnlyIn(Dist.CLIENT)
//    private static void handleClientData(PacketPlayAnimationAtPlayer message) {
//        Level level = Minecraft.getInstance().level;
//        if (Minecraft.getInstance().player == null || level == null) return;
//        if (level.getEntity(message.entityId()) != null) {
//            Player player = (Player) level.getEntity(message.entityId());
//            if (player == Minecraft.getInstance().player) return;
//            if (player instanceof AbstractClientPlayer clientPlayer) {
//                Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ResourceLocation
//                        .fromNamespaceAndPath(HackersAndSlashers.MODID, "player_animations"));
//                if (associatedData instanceof ModifierLayer<?> modifierLayer) {
//                    @SuppressWarnings("unchecked")
//                    var animation = (ModifierLayer<IAnimation>) modifierLayer;
//                    animation.replaceAnimationWithFade(
//                            AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
//                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath
//                                            (HackersAndSlashers.MODID, message.animationName())))
//                                    .playAnimation()
//                                    .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
//                                    .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(true)));
//                }
//            }
//        }
//    }


//    private static void playClientAnimation(Entity entity, String animationName) {
//        if (entity instanceof AbstractClientPlayer) {
//            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
//                    .get(ResourceLocation.fromNamespaceAndPath(MODID, "player_animations"));
//            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
//                @SuppressWarnings("unchecked")
//                var animation = (ModifierLayer<IAnimation>) modifierLayer;
//                if (!animation.isActive()) {
//                    animation.replaceAnimationWithFade(
//                            AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
//                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(
//                                            ResourceLocation.fromNamespaceAndPath(MODID, animationName)))
//                                    .playAnimation()
//                                    .setFirstPersonMode(getFirstPersonAlternatives())
//                                    .setFirstPersonConfiguration(new FirstPersonConfiguration()
//                                            .setShowRightArm(false)
//                                            .setShowLeftItem(true)));
//                }
//            }
//        }
//    }
}
