package net.f708.realisticforging.mixin;

import net.f708.realisticforging.mixin.utils.PlayerSafeAddAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(Player.class)
public abstract class SafeAddMixin implements PlayerSafeAddAccessor {

    @Shadow public abstract boolean addItem(ItemStack stack);

    @Shadow @Nullable public abstract ItemEntity drop(ItemStack itemStack, boolean includeThrowerName);

    @Unique
    public boolean safeAdd(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        // Создаем копию стака для безопасности
        ItemStack copyStack = stack.copy();

        // Пытаемся добавить в инвентарь
        boolean fullyAdded = addItem(copyStack);

        if (fullyAdded) {
            // Все предметы были добавлены успешно
            stack.setCount(0); // Очищаем оригинальный стак
            return true;
        } else {
            // Если остались предметы, выбрасываем их
            if (!copyStack.isEmpty()) {
                drop(copyStack, false);
            }

            // Обновляем оригинальный стак (показываем что было добавлено)
            stack.setCount(copyStack.getCount());
            return false; // Не все предметы были добавлены в инвентарь
        }
    }

    @Unique
    public void safeAddAndDrop(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        // Пытаемся добавить в инвентарь
        if (!addItem(stack)) {
            // Если не удалось добавить все, выбрасываем остатки
            if (!stack.isEmpty()) {
                drop(stack, false);
            }
        }
    }
}