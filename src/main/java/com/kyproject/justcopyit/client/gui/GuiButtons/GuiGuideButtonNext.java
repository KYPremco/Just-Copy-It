package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiGuideButtonNext extends GuiButton {

    final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/progress_book.png");
    int u = 3;
    int v = 194;

    public GuiGuideButtonNext(int buttonId, int x, int y) {
        super(buttonId, x, y, 18, 10, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.renderEngine.bindTexture(texture);
            if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                hovered = true;
            } else {
                hovered = false;
            }
            if(hovered) {
                u = 3;
            } else {
                u = 26;
            }
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
