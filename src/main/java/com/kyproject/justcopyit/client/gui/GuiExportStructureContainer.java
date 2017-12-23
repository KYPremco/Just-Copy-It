package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.*;
import com.kyproject.justcopyit.container.exportStructureContainer.ContainerExportStructure;
import com.kyproject.justcopyit.network.MessageHandleGuiExportButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.tileentity.TileEntityExport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiExportStructureContainer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/exportgui.png");

    GuiButtonMedium buttonExport;
    GuiButtonPlus buttonPlus;
    GuiButtonMinus buttonMinus;
    private GuiTextField text;
    private TileEntityExport te;


    private final int BUTTONEXPORT = 0, BUTTONPLUS = 1, BUTTONMINUS = 2, NAMETEXTBOX = 3;

    public GuiExportStructureContainer(InventoryPlayer player, TileEntityExport tileEntityExport) {
        super(new ContainerExportStructure(player, tileEntityExport));
        te = tileEntityExport;
        xSize = 177;
        ySize = 176;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.text.drawTextBox();
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(new TextComponentTranslation("tile.tutorial_container.name").getFormattedText(), 5, 5, Color.darkGray.getRGB());
        fontRenderer.drawString("Name", 5, 20, Color.black.getRGB());
        fontRenderer.drawString("Max uses:", 75, 80, Color.black.getRGB());
        fontRenderer.drawString("∞", 125, 80, Color.black.getRGB());
        fontRenderer.drawString("Export", 30, 77, Color.black.getRGB());
        fontRenderer.drawString("Status:", 8, 60, Color.black.getRGB());
        fontRenderer.drawString(te.getStateExport(), 45, 60, Color.black.getRGB());
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONEXPORT:
                NetworkHandler.sendToServer(new MessageHandleGuiExportButton(te, 0, text.getText()));
                break;
            case BUTTONPLUS:
                //NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 1));
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.text.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;

        buttonList.add(buttonExport = new GuiButtonMedium(BUTTONEXPORT, centerX + 27, centerY + 73));
        buttonList.add(buttonPlus = new GuiButtonPlus(BUTTONPLUS, centerX + 156, centerY + 78));
        buttonList.add(buttonMinus = new GuiButtonMinus(BUTTONMINUS, centerX + 140, centerY + 78));
        this.text = new GuiTextField(3, fontRenderer, centerX + 5,centerY + 30,100,10);
        text.setMaxStringLength(15);
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(this.text.isFocused()) {
            this.text.textboxKeyTyped(typedChar, keyCode);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.text.updateCursorCounter();
    }

    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
