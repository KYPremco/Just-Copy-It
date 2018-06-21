package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonRotate extends GuiButton {

    final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");
    int u = 0;
    int v = 36;

    public GuiButtonRotate(int buttonId, int x, int y) {
        super(buttonId, x, y, 18, 18, "");
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
                u = 18;
            } else {
                u = 0;
            }
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
