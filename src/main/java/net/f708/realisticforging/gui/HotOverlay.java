package net.f708.realisticforging.gui;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

public class HotOverlay {

    ResourceLocation FIRST = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "hot/firstlayer");
    ResourceLocation SECOND = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "hot/secondlayer");
    ResourceLocation THIRD = ResourceLocation.fromNamespaceAndPath(RealisticForging.MODID, "hot/thirdlayer");



    public ResourceLocation getFirstModel(){
        return FIRST;
    }
    public ResourceLocation getSecondModel(){
        return SECOND;
    }
    public ResourceLocation getThirdModel(){
        return THIRD;
    }

}
