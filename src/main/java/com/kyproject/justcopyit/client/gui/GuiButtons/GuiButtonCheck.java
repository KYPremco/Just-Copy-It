package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonCheck extends GuiButton {

    final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");
    public boolean checked;
    int u = 0;
    int v = 0;

    public GuiButtonCheck(int buttonId, int x, int y, boolean checked) {
        super(buttonId, x, y, 18, 18, "");
        this.checked = checked;
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
            if(checked) {
                v = 0;
            } else {
                v = 18;
            }
            drawTexturedModalRect(x, y, u, v, width, height);
        }
    }
}
