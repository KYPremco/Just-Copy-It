package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonCheck;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonMedium;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonUnchecked;
import com.kyproject.justcopyit.container.builderContainer.ContainerBuilder;
import com.kyproject.justcopyit.container.scannercontainer.ContainerScanner;
import com.kyproject.justcopyit.network.MessageHandleGuiBuilderButton;
import com.kyproject.justcopyit.network.MessageHandleGuiScannerButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import com.kyproject.justcopyit.tileentity.TileEntityScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiScannerContainer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/scannergui.png");

    GuiButtonMedium builderSaveButton;
    GuiButtonMedium builderLoadButton;
    GuiButtonCheck checkButton;
    GuiButtonUnchecked uncheckedButton;

    private TileEntityScanner te;


    final int BUTTONSAVE = 0, BUTTONLOAD = 1, CHECK = 2;

    public GuiScannerContainer(InventoryPlayer player, TileEntityScanner tileEntityScanner) {
        super(new ContainerScanner(player, tileEntityScanner));
        te = tileEntityScanner;
        xSize = 184;
        ySize = 116;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;
        List<String> tooltip = new ArrayList<>();
        tooltip.add("Skip missing blocks");
        drawTooltip(tooltip, mouseX, mouseY, centerX + 182, centerY + 72, 16,16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(new TextComponentTranslation("tile.scanner_container.name").getFormattedText(), 5, 5, Color.darkGray.getRGB());
        fontRenderer.drawString(new TextComponentTranslation("tile.scanner_container.copy").getFormattedText(), 70, 13, Color.black.getRGB());
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONSAVE:
                NetworkHandler.sendToServer(new MessageHandleGuiScannerButton(te, 0, Minecraft.getMinecraft().player.canUseCommand(4,null)));
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
    public void initGui() {
        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;

        buttonList.add(builderSaveButton = new GuiButtonMedium(BUTTONSAVE, centerX + 61, centerY + 8));

        super.initGui();
    }

    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
