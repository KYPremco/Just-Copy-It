package com.kyproject.justcopyit.client.gui.GuiButtons;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class GuiCustomTextField extends GuiTextField {

    private final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");
    private int u = 217;
    private int v = 14;

    public GuiCustomTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height, int u, int v) {
        super(componentId, fontrendererObj, x, y, width, height);
        this.u = u;
        this.v = v;
    }




}
