package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.*;
import com.kyproject.justcopyit.network.MessageHandleGuiBuilderButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiRemote extends GuiScreen {

    private static ResourceLocation texture;
    private BlockPos pos;
    private TileEntityBuilder te;
    private int guiWidth = 104;
    private int guiHeight = 90;

    private GuiGuideButtonClose buttonClose;
    private GuiButtonPlus buttonPlus;
    private GuiButtonMinus buttonMinus;
    private GuiButtonReset buttonReset;
    private GuiButtonCheck buttonCheck;
    private GuiButtonMedium buttonMedium;
    private GuiButtonRotate buttonRotate;

    private final int  BUTTONSTART = 1, CHECK = 2, BUTTONCLOSE = 3, BUTTONPLUSX = 4, BUTTONMINUSX = 5 ,BUTTONPLUSY = 6, BUTTONMINUSY = 7,BUTTONPLUSZ = 8, BUTTONMINUSZ = 9, BUTTONRESETX = 10, BUTTONRESETY = 11, BUTTONRESETZ = 12, BUTTONROTATE = 13;

    public GuiRemote(World world, int x, int y, int z) {
        texture = new ResourceLocation(JustCopyIt.MODID + ":textures/gui/remotegui.png");
        pos = new BlockPos(x,y,z);
        te = (TileEntityBuilder) world.getTileEntity(pos);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 0.7f);
        drawTexturedModalRect(width - guiWidth, 0, 0, 0, guiWidth, guiHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);


        fontRenderer.drawString("Build", width - guiWidth + 15, 68, Color.black.getRGB());

        fontRenderer.drawString("X:", width - guiWidth + 5, 9, Color.white.getRGB());
        fontRenderer.drawString("Y:", width - guiWidth + 5, 27, Color.white.getRGB());
        fontRenderer.drawString("Z:", width - guiWidth + 5, 46, Color.white.getRGB());

        fontRenderer.drawString(String.valueOf(te.movableX), width - guiWidth + 15, 9, Color.white.getRGB());
        fontRenderer.drawString(String.valueOf(te.movableY), width - guiWidth + 15, 27, Color.white.getRGB());
        fontRenderer.drawString(String.valueOf(te.movableZ), width - guiWidth + 15, 46, Color.white.getRGB());

        List<String> tooltip = new ArrayList<>();
        tooltip.add("Skip missing blocks");
        this.drawTooltip(tooltip, mouseX, mouseY, width - guiWidth + 50, 63, 16,16);
    }



    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {

        buttonList.clear();
        buttonList.add(buttonClose = new GuiGuideButtonClose(BUTTONCLOSE, width - 12, 7));

        buttonList.add(buttonPlus = new GuiButtonPlus(BUTTONPLUSX, width - guiWidth + 45, 7));
        buttonList.add(buttonMinus = new GuiButtonMinus(BUTTONMINUSX, width - guiWidth + 60, 7));
        buttonList.add(buttonReset = new GuiButtonReset(BUTTONRESETX, width - guiWidth + 75, 7));

        buttonList.add(buttonPlus = new GuiButtonPlus(BUTTONPLUSY, width - guiWidth + 45, 25));
        buttonList.add(buttonMinus = new GuiButtonMinus(BUTTONMINUSY, width - guiWidth + 60, 25));
        buttonList.add(buttonReset = new GuiButtonReset(BUTTONRESETY, width - guiWidth + 75, 25));

        buttonList.add(buttonPlus = new GuiButtonPlus(BUTTONPLUSZ, width - guiWidth + 45, 44));
        buttonList.add(buttonMinus = new GuiButtonMinus(BUTTONMINUSZ, width - guiWidth + 60, 44));
        buttonList.add(buttonReset = new GuiButtonReset(BUTTONRESETZ, width - guiWidth + 75, 44));

        buttonList.add(buttonMedium = new GuiButtonMedium(BUTTONSTART, width - guiWidth + 5, 63));
        buttonList.add(buttonCheck = new GuiButtonCheck(CHECK, width - guiWidth + 50, 63, te.getChecked()));

        buttonList.add(buttonRotate = new GuiButtonRotate(BUTTONROTATE, width - guiWidth + 70, 63));


        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONSTART:
                te.startStructure();
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONSTART));
                break;
            case CHECK:
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, CHECK));
                buttonCheck.checked = !buttonCheck.checked;
                break;

            case BUTTONCLOSE:
                mc.displayGuiScreen(null);
                break;
            case BUTTONPLUSX:
                te.buttonPressed(BUTTONPLUSX);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONPLUSX));
                break;
            case BUTTONMINUSX:
                te.buttonPressed(BUTTONMINUSX);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONMINUSX));
                break;
            case BUTTONPLUSY:
                te.buttonPressed(BUTTONPLUSY);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONPLUSY));
                break;
            case BUTTONMINUSY:
                te.buttonPressed(BUTTONMINUSY);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONMINUSY));
                break;
            case BUTTONPLUSZ:
                te.buttonPressed(BUTTONPLUSZ);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONPLUSZ));
                break;
            case BUTTONMINUSZ:
                te.buttonPressed(BUTTONMINUSZ);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONMINUSZ));
                break;
            case BUTTONRESETX:
                te.buttonPressed(BUTTONRESETX);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONRESETX));
                break;
            case BUTTONRESETY:
                te.buttonPressed(BUTTONRESETY);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONRESETY));
                break;
            case BUTTONRESETZ:
                te.buttonPressed(BUTTONRESETZ);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONRESETZ));
                break;
            case BUTTONROTATE:
                te.buttonPressed(BUTTONROTATE);
                NetworkHandler.sendToServer(new MessageHandleGuiBuilderButton(te, BUTTONROTATE));
                break;
                default:
                    break;
        }

        super.actionPerformed(button);
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
