package net.f708.realisticforging.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCookingRecipe.class)
public class AbstractCookingRecipeMixin {
    @WrapOperation(
            method = "assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private static ItemStack copyComponent(ItemStack instance, Operation<ItemStack> original,
                                           @Local(argsOnly = true) SingleRecipeInput input) {
        ItemStack newItem = original.call(instance);
        ItemStack oldItem = input.item();
        if (oldItem.has(DataComponents.CUSTOM_NAME)) {
            newItem.set(DataComponents.CUSTOM_NAME, oldItem.get(DataComponents.CUSTOM_NAME));
        }
        return newItem;
    }
}