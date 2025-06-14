package net.f708.realisticforging.gui;

import net.f708.realisticforging.RealisticForging;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@OnlyIn(Dist.CLIENT)
public class ModScreen extends Screen {
    private static final ResourceLocation SPRITE =
            ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "textures/hot/model.png");

    public ModScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init(); // Обязательный вызов
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick); // Фон
        int x = this.width / 2 - 32;
        int y = this.height / 2 - 32;
        guiGraphics.blit(SPRITE, x, y, 0, 0, 64, 64); // Текстура
        super.render(guiGraphics, mouseX, mouseY, partialTick); // Тултипы
    }
}