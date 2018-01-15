package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonCheck;
import com.kyproject.justcopyit.client.gui.GuiButtons.GuiButtonMedium;
import com.kyproject.justcopyit.container.builderContainer.ContainerBuilder;
import com.kyproject.justcopyit.network.MessageHandleGuiBuilderButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiBuilderContainer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buildergui.png");
    private static final ResourceLocation energytex = new ResourceLocation(JustCopyIt.MODID, "textures/gui/buttons.png");

    private GuiButtonMedium buttonMedium;
    private GuiButtonCheck checkButton;
    private GuiButtonCheck checkButtonOverwrite;

    private TileEntityBuilder te;


    private final int BUTTONSTART = 1, CHECK = 2, BUTTONDEMOLISH = 3, BUTTONOVERWRITE = 14;

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
        List<String> skip = new ArrayList<>();
        skip.add(new TextComponentTranslation("tile.builder_container.skip").getFormattedText());
        drawTooltip(skip, mouseX, mouseY, centerX + 201, centerY + 150, 18,18);
        List<String> overwrite = new ArrayList<>();
        overwrite.add(new TextComponentTranslation("tile.builder_container.overwrite").getFormattedText());
        drawTooltip(overwrite, mouseX, mouseY, centerX + 221, centerY + 150, 18,18);
        List<String> energy = new ArrayList<>();
        energy.add(NumberFormat.getNumberInstance(Locale.US).format(te.energy.getEnergyStored()) + " / " + NumberFormat.getNumberInstance(Locale.US).format(te.energy.getMaxEnergyStored()) + " RF");
        this.drawTooltip(energy, mouseX, mouseY, guiLeft + 18, guiTop + 151, 94, 16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(new TextComponentTranslation("tile.builder_container.name").getFormattedText(), 5, 5, Color.darkGray.getRGB());
        fontRenderer.drawString(new TextComponentTranslation("tile.builder_container.build").getFormattedText(), 168, 156, Color.black.getRGB());
        fontRenderer.drawString(new TextComponentTranslation("tile.builder_container.destroy").getFormattedText(), 122, 156, Color.black.getRGB());
    }



    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONSTART:
                te.startStructure();
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 1));
                break;
            case CHECK:
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, 2));
                checkButton.checked = !checkButton.checked;
                break;
            case BUTTONDEMOLISH:
                te.startDemolish();
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONDEMOLISH));
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        Minecraft.getMinecraft().getTextureManager().bindTexture(energytex);
        drawTexturedModalRect(guiLeft + 18, guiTop + 151, 0,240, te.getEnergy(), 16);
    }

    @Override
    public void initGui() {
        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;

        buttonList.add(buttonMedium = new GuiButtonMedium(BUTTONSTART, centerX + 157, centerY + 150));
        buttonList.add(checkButton = new GuiButtonCheck(CHECK, centerX + 201, centerY + 150, te.getChecked()));
        buttonList.add(buttonMedium = new GuiButtonMedium(BUTTONDEMOLISH, centerX + 114, centerY + 150));
        buttonList.add(checkButtonOverwrite = new GuiButtonCheck(BUTTONOVERWRITE, centerX + 221, centerY + 150, true));

        super.initGui();
    }

    private void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
