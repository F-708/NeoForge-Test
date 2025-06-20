package net.f708.realisticforging.gui;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

public class HotOverlay extends GuiGraphics {

    ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "hot/model");


    public HotOverlay(Minecraft minecraft, MultiBufferSource.BufferSource bufferSource) {
        super(minecraft, bufferSource);
    }

    @Override
    public void hLine(int minX, int maxX, int y, int color) {
        renderItem(ModItems.BARSHAPEDMOLD.get().getDefaultInstance(), 20, 20);
    }

    public ResourceLocation getMODEL() {
        return MODEL;
    }
}
