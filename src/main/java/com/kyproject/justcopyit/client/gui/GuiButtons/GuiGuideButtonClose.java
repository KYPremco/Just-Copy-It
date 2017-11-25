package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiGuideButtonClose extends GuiButton {

    final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/progress_book.png");

    int v = 194;
    int u = 128;

    public GuiGuideButtonClose(int buttonId, int x, int y) {
        super(buttonId, x, y, 5, 6, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.renderEngine.bindTexture(texture);
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
