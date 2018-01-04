package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonCheck;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonMedium;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonUnchecked;
import com.kyproject.justcopyit.container.builderContainer.ContainerBuilder;
import com.kyproject.justcopyit.network.MessageHandleGuiBuilderButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiBuilderContainer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buildergui.png");
    private static final ResourceLocation energytex = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");

    GuiButtonMedium builderLoadButton;
    GuiButtonCheck checkButton;

    private TileEntityBuilder te;


    final int BUTTONLOAD = 1, CHECK = 2;

    public GuiBuilderContainer(InventoryPlayer player, TileEntityBuilder tileEntityBuilder) {
        super(new ContainerBuilder(player, tileEntityBuilder));
        te = tileEntityBuilder;
        xSize = 256;
        ySize = 256;
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
        List<String> energy = new ArrayList<>();
        energy.add(NumberFormat.getNumberInstance(Locale.US).format(te.energy.getEnergyStored()) + " / " + NumberFormat.getNumberInstance(Locale.US).format(te.energy.getMaxEnergyStored()) + " RF");
        this.drawTooltip(energy, mouseX, mouseY, guiLeft + 48, guiTop + 148, 94, 16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(new TextComponentTranslation("tile.builder_container.name").getFormattedText(), 5, 5, Color.darkGray.getRGB());
        fontRenderer.drawString(new TextComponentTranslation("tile.builder_container.build").getFormattedText(), 155, 152, Color.black.getRGB());
    }



    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONLOAD:
                te.startStructure();
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 1));
                break;
            case CHECK:
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 2));
                checkButton.checked = !checkButton.checked;
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        Minecraft.getMinecraft().getTextureManager().bindTexture(energytex);
        drawTexturedModalRect(guiLeft + 48, guiTop + 148, 0,240, te.getEnergy(), 16);
    }

    @Override
    public void initGui() {
        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;

        buttonList.add(builderLoadButton = new GuiButtonMedium(BUTTONLOAD, centerX + 145, centerY + 147));
        buttonList.add(checkButton = new GuiButtonCheck(CHECK, centerX + 190, centerY + 147, te.getChecked()));

        super.initGui();
    }

    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
