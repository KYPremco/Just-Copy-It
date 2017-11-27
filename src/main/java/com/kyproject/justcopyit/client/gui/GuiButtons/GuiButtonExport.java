package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonExport extends GuiButton {

    private final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");
    private int u = 119;
    private int v = 14;

    public GuiButtonExport(int buttonId, int x, int y) {
        super(buttonId, x, y, 40, 16, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.renderEngine.bindTexture(texture);
            hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

            if(hovered) {
                u = 162;
            } else {
                u = 119;
            }
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
