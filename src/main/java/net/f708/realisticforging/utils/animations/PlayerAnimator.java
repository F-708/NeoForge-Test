package net.f708.realisticforging.utils.animations;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.utils.TickScheduler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(modid = RealisticForging.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlayerAnimator {

    /**
     * Setups the player animator
     * @param event The event registerer.
     */

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RealisticForging.LOGGER.info("Registering animation factory");
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_animations"),
                100,
                PlayerAnimator::registerPlayerAnimation);
    }



    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> animationLayer = new ModifierLayer<>();

        AdjustmentModifier adjustmentModifier = new AdjustmentModifier((partName) -> {

            float rotationX = 0;
            float rotationY = 0;
            float rotationZ = 0;



            float pitchRadians = (float) Math.toRadians(player.getXRot() / 2F);



            switch (partName) {
                case "torso" -> {
                    rotationX = (pitchRadians * 0.30F);
                }
                case "body" -> {
                    rotationX = -(pitchRadians * 0.3F);
                }
                case "rightArm", "leftArm" -> {
                    rotationX = pitchRadians;
                }
                default -> {
                    return Optional.empty();
                }
            }

            return Optional.of(new AdjustmentModifier.PartModifier(
                    new Vec3f(rotationX, rotationY, rotationZ),
                    new Vec3f(0, 0, 0),
                    new Vec3f(0, 0, 0)
            ));


        });
        animationLayer.addModifier(adjustmentModifier, 0);
        adjustmentModifier.fadeOut(20);
        return animationLayer;
    }

    /**
     * This method, when called, plays an animation at the entity passed as parameter.
     * It takes a nullable event, so it can be called at client side.
     * @param world The world/level the animation will be played.
     * @param entity The target entity.
     * @param animationName The animation to be played.
     */


    public static void playAnimation(LevelAccessor world, Entity entity, String animationName, Boolean RH, int fadeInTicks) {
        try {
            if (world.isClientSide()) {
                playClientAnimation(entity, animationName, RH, fadeInTicks);
            } else {
                playServerAnimation(world, entity, animationName, RH, fadeInTicks);
            }
        } catch (Exception e) {
            RealisticForging.LOGGER.error("Error in PlayerAnimator::playAnimation: {}", e.getMessage(), e);
        }
    }


    public static void cancelAnimation(LevelAccessor world, Entity entity) {
        try {
            if (world.isClientSide()) {
                cancelClientAnimation(entity);
            } else {
                cancelServerAnimation(world, entity);
            }
        } catch (Exception e) {
            RealisticForging.LOGGER.error("Error in PlayerAnimator::cancelAnimation: {}", e.getMessage(), e);
        }
    }

    private static void cancelServerAnimation(LevelAccessor world, Entity entity) {
        if (!world.isClientSide() && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity,
                    new net.f708.realisticforging.network.packets.PacketServerCancelAnimation());
        }
    }

    public static void cancelClientAnimation(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
                    .get(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_animations"));
            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
                @SuppressWarnings("unchecked")
                var animation = (ModifierLayer<IAnimation>) modifierLayer;
                animation.setAnimation(null);
            }
        }
    }

//    private static void playClientAnimation(Entity entity, String animationName) {
//        if (entity instanceof AbstractClientPlayer) {
//            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
//                    .get(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_animations"));
//            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
//                @SuppressWarnings("unchecked")
//                var animation = (ModifierLayer<IAnimation>) modifierLayer;
//                if (!animation.isActive()) {
//                    animation.replaceAnimationWithFade(
//                            AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
//                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(
//                                            ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, animationName)))
//                                    .playAnimation()
//                                    .setFirstPersonMode(getFirstPersonAlternatives())
//                                    .setFirstPersonConfiguration(new FirstPersonConfiguration()
//                                            .setShowRightArm(false)
//                                            .setShowLeftItem(true)));
//                }
//            }
//        }
//    }

    private static void playClientAnimation(Entity entity, String animationName, Boolean RH, int fadeInTicks) {
        if (entity instanceof AbstractClientPlayer) {
            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
                    .get(ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "player_animations"));
            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
                @SuppressWarnings("unchecked")
                var animation = (ModifierLayer<IAnimation>) modifierLayer;
                MirrorModifier mirror = new MirrorModifier();
                if (RH) {
                    if (modifierLayer.size() > 1) {
                        modifierLayer.removeModifier(1);
                    }
                } else {
                    if (modifierLayer.size() <=1) {
                        modifierLayer.addModifier(mirror, 1);
                    }
                }
                    animation.replaceAnimationWithFade(
                            AbstractFadeModifier.functionalFadeIn(fadeInTicks, (modelName, type, value) -> value),
                            Objects.requireNonNull(PlayerAnimationRegistry.getAnimation(
                                            ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, animationName)))
                                    .playAnimation()
                                    .setFirstPersonMode(getFirstPersonAlternatives())
                                    .setFirstPersonConfiguration(new FirstPersonConfiguration()
                                            .setShowRightArm(false)
                                            .setShowLeftItem(true)));
            }
        }
    }


    // Used to specify if a first person changing mod is installed
    private static FirstPersonMode getFirstPersonAlternatives() {
        if (ModList.get().isLoaded("firstperson")) {
            return FirstPersonMode.DISABLED;
        } else {
            return FirstPersonMode.THIRD_PERSON_MODEL;
        }
    }

    private static void playServerAnimation(LevelAccessor world, Entity entity, String animationName, boolean RH, int fadeInTicks) {
        if (!world.isClientSide() && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity,
                    new net.f708.realisticforging.network.packets.PacketPlayAnimationAtPlayer(animationName, entity.getId(), false, RH, fadeInTicks));
        }
    }


}