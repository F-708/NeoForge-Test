package net.f708.realisticforging.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.TEST.ItemInTongs;
import net.f708.realisticforging.component.ModDataComponents;
import net.f708.realisticforging.item.ModItems;
import net.f708.realisticforging.item.custom.TongsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed);

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderPasses(Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;"
            )
    )
    private List<BakedModel> addAdditionalModel(BakedModel instance, ItemStack itemStack, boolean fabulous, Operation<List<BakedModel>> original) {
        List<BakedModel> list = original.call(instance, itemStack, fabulous);

        if (!(itemStack.getItem() instanceof TongsItem)) return list;

        list = new ArrayList<>(list);

        ItemInTongs itemInTongs = itemStack.get(ModDataComponents.ITEM_IN_TONGS);
        if (itemInTongs == null || itemInTongs.stack().isEmpty()) {
            list.add(getModel(ModItems.TONGS.get().getDefaultInstance(), null, null, 0));
        } else {
            list.clear();

            BakedModel leftModel = Minecraft.getInstance().getModelManager().getModel(
                    ModelResourceLocation.standalone( // Используйте тот же вариант, что и при регистрации
                            ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "item/tongs_left")
                    )
            );
            BakedModel rightModel = Minecraft.getInstance().getModelManager().getModel(
                    ModelResourceLocation.standalone( // Используйте тот же вариант, что и при регистрации
                            ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "item/tongs_right")
                    )
            );

            list.add(leftModel);

            list.add(getModel(itemInTongs.stack(), null, null, 0));

            list.add(rightModel);
        }

        return list;
    }
}