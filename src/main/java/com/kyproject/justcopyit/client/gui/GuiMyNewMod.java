package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiGuideButtonClose;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiGuideButtonNext;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiGuideButtonPrevious;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiMyNewMod extends GuiScreen {


    private int currentPage = 0;
    private static final int bookTotalPages = 2;
    private static ResourceLocation[] bookPageTexture = new ResourceLocation[bookTotalPages];
    private static String[] stringPageText = new String[bookTotalPages];
    int guiWidth = 254;
    int guiHeight = 180;

    GuiGuideButtonClose buttonClose;
    GuiGuideButtonNext buttonNext;
    GuiGuideButtonPrevious buttonPrevious;

    final int  BUTTONCLOSE = 0, BUTTONNEXT = 1, BUTTONPREVIOUS = 2;

    public GuiMyNewMod() {
        bookPageTexture[0] = new ResourceLocation(JustCopyIt.MODID + ":textures/gui/progress_book.png");
        bookPageTexture[1] = new ResourceLocation(JustCopyIt.MODID + ":textures/gui/progress_book.png");

        stringPageText[0] = "Hello world";
        stringPageText[1] = "Hello page 2\ntest";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        drawDefaultBackground();
        GlStateManager.pushMatrix();
        {
            if (currentPage == 0) {
                Minecraft.getMinecraft().renderEngine.bindTexture(bookPageTexture[0]);
            } else {
                Minecraft.getMinecraft().renderEngine.bindTexture(bookPageTexture[1]);
            }
            drawTexturedModalRect(centerX, centerY, 1, 1, guiWidth, guiHeight);
        }
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRenderer.drawSplitString(stringPageText[currentPage], centerX + 18, centerY + 18,90, 0x000);
    }

    public void updateButtons() {
        if(currentPage < bookTotalPages - 1) {
            buttonNext.visible = true;
        } else {
            buttonNext.visible = false;
        }

        if(currentPage > 0) {
            buttonPrevious.visible = true;
        } else {
            buttonPrevious.visible = false;
        }
    }

    public void updateTextBoxes() {

    }

    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        buttonList.clear();
        buttonList.add(buttonClose = new GuiGuideButtonClose(BUTTONCLOSE, centerX + guiWidth - 22, centerY + 12));
        buttonList.add(buttonNext = new GuiGuideButtonNext(BUTTONNEXT, centerX + guiWidth - 47, centerY + guiHeight - 25));
        buttonList.add(buttonPrevious = new GuiGuideButtonPrevious(BUTTONPREVIOUS, centerX + 29, centerY + guiHeight - 25));
        buttonPrevious.visible = false;

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONCLOSE:
                mc.displayGuiScreen(null);
                break;
            case BUTTONNEXT:
                if(currentPage < bookTotalPages - 1) {
                    currentPage++;
                }
                break;
            case BUTTONPREVIOUS:
                if(currentPage > 0) {
                    currentPage--;
                }
                break;
        }
        updateButtons();

        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        updateTextBoxes();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_E) {
            mc.displayGuiScreen(null);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
