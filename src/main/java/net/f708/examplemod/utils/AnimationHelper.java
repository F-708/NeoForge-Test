package net.f708.examplemod.utils;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.network.packets.PacketPlayAnimationAtPlayer;
import net.f708.examplemod.network.packets.PacketServerCancelAnimation;
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

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AnimationHelper {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ExampleMod.LOGGER.info("Registering animation factory");
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "player_animations"),
                42,
                AnimationHelper::registerPlayerAnimation);
    }

    public static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> animationLayer = new ModifierLayer<>();

        AdjustmentModifier adjustmentModifier = new AdjustmentModifier((partName) -> {
            float rotationX = 0;
            float rotationY = 0;
            float rotationZ = 0;

            float pitchRadians = (float) Math.toRadians(player.getXRot() / 2f);

            switch (partName) {
                case "torso" -> rotationX = (pitchRadians * 0.75f);
                case "body" -> {
                    rotationX = -(pitchRadians * 0.5F);
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
        return animationLayer;

    }


    public static void playAnimation(LevelAccessor world, Entity entity, String animationName) {
        try {
            if (world.isClientSide()) {
                playClientAnimation(entity, animationName);
            } else {
                playServerAnimation(world, entity, animationName);
            }
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Error in PlayerAnimator::playAnimation: {}", e.getMessage(), e);
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
            ExampleMod.LOGGER.error("Error in PlayerAnimator::cancelAnimation: {}", e.getMessage(), e);
        }
    }

    private static void cancelServerAnimation(LevelAccessor world, Entity entity) {
        if (!world.isClientSide() && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity,
                    new PacketServerCancelAnimation());
        }
    }

    public static void cancelClientAnimation(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            Object associatedData = PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) entity)
                    .get(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "player_animations"));
            if (associatedData instanceof ModifierLayer<?> modifierLayer) {
                @SuppressWarnings("unchecked")
                var animation = (ModifierLayer<IAnimation>) modifierLayer;
                animation.setAnimation(null);
            }
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

    private static void playServerAnimation(LevelAccessor world, Entity entity, String animationName) {
        if (!world.isClientSide() && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity,
                    new PacketPlayAnimationAtPlayer(animationName, entity.getId(), false));
        }
    }


    private static FirstPersonMode getFirstPersonAlternatives() {
        if (ModList.get().isLoaded("firstperson")) {
            return FirstPersonMode.DISABLED;
        } else {
            return FirstPersonMode.THIRD_PERSON_MODEL;
        }
    }
}
