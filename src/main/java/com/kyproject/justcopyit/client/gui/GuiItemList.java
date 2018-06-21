package com.kyproject.justcopyit.client.gui;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.gui.GuiButtons.*;
import com.kyproject.justcopyit.network.MessageHandleGuiBuilderButton;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.templates.StructureTemplateManager;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import com.kyproject.justcopyit.util.NBTUtilFix;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import scala.Int;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GuiItemList extends GuiScreen {

    private StructureTemplateManager structureTemplateManager;
    private static ResourceLocation texture;
    private World world;

    private int maxItemsPerPage = 10;
    private int currentPage = 0;
    private List<IBlockState> RAWBLOCKSSTATE = new ArrayList<>();
    private List<List<ItemStack>> itemsInBook = new ArrayList<>();
    private List<List<String>> itemNamesInBook = new ArrayList<>();
    private List<List<Integer>> itemCountInBook = new ArrayList<>();

    private int guiWidth = 254;
    private int guiHeight = 220;

    private GuiGuideButtonClose buttonClose;
    private GuiGuideButtonNext buttonNext;
    private GuiGuideButtonPrevious buttonPrevious;

    private final int BUTTONNEXT = 1, BUTTONPREV = 2, BUTTONCLOSE = 3;

    public GuiItemList(NBTTagCompound blueprintNBT, World world) {
        texture = new ResourceLocation(JustCopyIt.MODID + ":textures/gui/book.png");
        this.world = world;

        this.createBook(blueprintNBT);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.pushMatrix();
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            GlStateManager.enableLight(1);
            drawTexturedModalRect(width / 2 - guiWidth / 2, height / 2 - guiHeight / 2, 1, 0, guiWidth, guiHeight);
        }
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRenderer.drawSplitString( currentPage + 1 + " / " + itemsInBook.size(), (width / 2 - guiWidth / 2) + 60 ,  (height / 2 - guiHeight / 2) + 41 +  157,90, 0x000);

        if(itemsInBook.size() != 0) {
            if(itemsInBook.get(currentPage).size() > 5) {
                for(int i = 0; i < itemsInBook.get(currentPage).size() - 5;i++) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemsInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 17,  (height / 2 - guiHeight / 2) + 23 +  i * 35);
                    fontRenderer.drawSplitString(Integer.toString(itemCountInBook.get(currentPage).get(i)), (width / 2 - guiWidth / 2) + 37 ,  (height / 2 - guiHeight / 2) + 28 +  i * 35,90, 0x000);
                    fontRenderer.drawSplitString(itemNamesInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 19 ,  (height / 2 - guiHeight / 2) + 41 +  i * 35,90, 0x000);
                }

                for(int i = 5; i < itemNamesInBook.get(currentPage).size();i++) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemsInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 127,  (height / 2 - guiHeight / 2) + 23 +  (i - 5) * 35);
                    fontRenderer.drawSplitString(Integer.toString(itemCountInBook.get(currentPage).get(i)), (width / 2 - guiWidth / 2) + 147 ,  (height / 2 - guiHeight / 2) + 28 +  (i - 5) * 35,90, 0x000);
                    fontRenderer.drawSplitString(itemNamesInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 129 ,  (height / 2 - guiHeight / 2) + 41 +  (i - 5) * 35,90, 0x000);
                }
            } else {
                for(int i = 0; i < itemNamesInBook.get(currentPage).size();i++) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemsInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 17,  (height / 2 - guiHeight / 2) + 23 +  i * 35);
                    fontRenderer.drawSplitString(Integer.toString(itemCountInBook.get(currentPage).get(i)), (width / 2 - guiWidth / 2) + 37 ,  (height / 2 - guiHeight / 2) + 28 +  i * 35,90, 0x000);
                    fontRenderer.drawSplitString(itemNamesInBook.get(currentPage).get(i), (width / 2 - guiWidth / 2) + 19 ,  (height / 2 - guiHeight / 2) + 41 +  i * 35,90, 0x000);
                }
            }
        } else {
            fontRenderer.drawSplitString("No data found\nClient sided\n\n\nWork in progress", (width / 2 - guiWidth / 2) + 18 ,  (height / 2 - guiHeight / 2) + 18,90, 0x000);
        }
    }



    public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
        if(mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();

        buttonList.add(buttonClose = new GuiGuideButtonClose(BUTTONCLOSE, width / 2 + guiWidth / 2 - 20, height / 2 - guiHeight / 2 + 15));
        buttonList.add(buttonNext = new GuiGuideButtonNext(BUTTONNEXT, width / 2 + guiWidth / 2 - 43, height / 2 + guiHeight / 2 - 25));
        buttonList.add(buttonPrevious = new GuiGuideButtonPrevious(BUTTONPREV, width / 2 - guiWidth / 2 + 25, height / 2 + guiHeight / 2 - 25));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONNEXT:
                if(currentPage < itemNamesInBook.size() - 1 ) {
                    currentPage++;
                }
                break;
            case BUTTONPREV:
                if(currentPage > 0) {
                    currentPage--;
                }
                break;
            case BUTTONCLOSE:
                mc.displayGuiScreen(null);
            default:
                break;
        }
        super.actionPerformed(button);
    }

    private void createBook(NBTTagCompound blueprintNBT) {
        StructureTemplateManager structureTemplateManager = new StructureTemplateManager(world);

        if(structureTemplateManager.readNBTFile(blueprintNBT.getString("name")) != null) {
            NBTTagList tagList = structureTemplateManager.readNBTFile(blueprintNBT.getString("name")).getTagList("blocks", Constants.NBT.TAG_COMPOUND);
            // Add all raw blockstate to variable
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                RAWBLOCKSSTATE.add(NBTUtilFix.readBlockState(tag));
            }

            List<ItemStack> uniqueItemList = this.createUniqueItemList();
            for (int i = 0; i <= uniqueItemList.size(); i++) {
                if (i % maxItemsPerPage == 1) {
                    List<String> itemName = new ArrayList<>();
                    List<ItemStack> itemStack = new ArrayList<>();
                    List<Integer> itemCount = new ArrayList<>();
                    for (int x = i; x < i + maxItemsPerPage; x++) {
                        if (!(x > uniqueItemList.size())) {
                            itemName.add(uniqueItemList.get(x - 1).getDisplayName());
                            itemStack.add(uniqueItemList.get(x - 1));
                            itemCount.add(this.countItem(uniqueItemList.get(x - 1).getDisplayName()));
                        } else {
                            break;
                        }
                    }
                    itemNamesInBook.add(itemName);
                    itemsInBook.add(itemStack);
                    itemCountInBook.add(itemCount);
                }
            }
        }

    }

    private List<ItemStack> createUniqueItemList() {
        List<String> uniqueItemNameList = new ArrayList<>();
        List<ItemStack> uniqueItemList = new ArrayList<>();

        for(IBlockState state : RAWBLOCKSSTATE) {
            if(!uniqueItemNameList.contains(this.getItem(state).getDisplayName())) {
                uniqueItemList.add(this.getItem(state));
                uniqueItemNameList.add(this.getItem(state).getDisplayName());
            }
        }
        return uniqueItemList;
    }

    private Integer countItem(String name) {
        int countItem = 0;

        for(IBlockState state : RAWBLOCKSSTATE) {
            if(this.getItem(state).getDisplayName().equals(name)) {
                countItem++;
            }
        }

        return countItem;
    }

    private ItemStack getItem(IBlockState blockState) {
        Block block = blockState.getBlock();

        if(block.getDefaultState().getMaterial().isLiquid()) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
        }
        return block.getItem(world, new BlockPos(0,0,0), blockState);
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
