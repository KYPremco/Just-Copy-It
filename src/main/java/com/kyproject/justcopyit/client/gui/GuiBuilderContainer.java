package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiBuilderSaveButton;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiBuilderLoadButton;
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
import java.util.List;

public class GuiBuilderContainer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buildergui.png");

    GuiBuilderSaveButton builderSaveButton;
    GuiBuilderLoadButton builderLoadButton;
    private TileEntityBuilder te;


    final int BUTTONSAVE = 0, BUTTONLOAD = 1;

    public GuiBuilderContainer(InventoryPlayer player, TileEntityBuilder tileEntityBuilder) {
        super(new ContainerBuilder(player, tileEntityBuilder));
        te = tileEntityBuilder;
        xSize = 229;
        ySize = 222;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(new TextComponentTranslation("tile.tutorial_container.name").getFormattedText(), 5, 5, Color.darkGray.getRGB());

    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONSAVE:
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 0));
                break;
            case BUTTONLOAD:
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 1));
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

        buttonList.add(builderSaveButton = new GuiBuilderSaveButton(BUTTONSAVE, centerX + 182, centerY + 76));
        buttonList.add(builderLoadButton = new GuiBuilderLoadButton(BUTTONLOAD, centerX + 202, centerY + 76));

        super.initGui();
    }

    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
