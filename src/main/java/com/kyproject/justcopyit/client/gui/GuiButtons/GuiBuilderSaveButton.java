package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiBuilderSaveButton extends GuiButton {

    final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");
    int u = 19;
    int v = 0;

    public GuiBuilderSaveButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 16, 16, "");
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

            }
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
