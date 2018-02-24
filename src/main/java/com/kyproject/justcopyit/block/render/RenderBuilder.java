package com.kyproject.justcopyit.block.render;

import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderBuilder extends TileEntitySpecialRenderer<TileEntityBuilder> {

    @Override
    public void render(TileEntityBuilder te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);


        EnumFacing facing = te.facing;
        if(facing == null) {
            facing = EnumFacing.getFront(te.getBlockMetadata());
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        double posX = te.rangeX;
        double posY = te.rangeY;
        double posZ = te.rangeZ;
        if(posX != 0 || posY != 0 || posZ != 0) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.translate(x, y, z);
        Color color = new Color(255, 255, 0, 50);
        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glLineWidth(3F);
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        double width = 0.5;
        double exPosX = 0;
        double exPosZ = 0;
        switch (facing) {
            case NORTH:
                exPosX = 0;
                exPosZ = -1;
                break;
            case EAST:
                exPosX = 1;
                exPosZ = 0;
                break;
            case WEST:
                exPosX = -1;
                exPosZ = 0;
                break;
            case SOUTH:
                exPosX = 0;
                exPosZ = 1;
                break;
            default:
                break;
        }

            GlStateManager.pushMatrix();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 175, 240);

            //First block
            //Blue
            bufferbuilder.pos(te.movableX + exPosX + posX + width, te.movableY + width, te.movableZ + exPosZ + width).color(0, 0, 255, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + width).color(0, 0, 255, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + posX + width, posY + te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, posY  + te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + posX  + width, te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + posX  + width, posY  + te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right end

            //Green
            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + width).color(0, 255, 0, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, posY + te.movableY + width, te.movableZ + exPosZ + width).color(0, 255, 0, 255).endVertex(); // right end

            //Second block
            bufferbuilder.pos(te.movableX + exPosX + posX  + width, te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + posX  + width, posY  + te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end

            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, posY  + te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end

            bufferbuilder.pos(te.movableX + exPosX + posX + width, te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + posX + width, posY + te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, posY  + te.movableY + width, te.movableZ + exPosZ + width + posZ).color(192,192,192, 255).endVertex(); // right start

            //Connector block
            //Red
            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + posZ + width).color(255, 0, 0, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, te.movableY + width, te.movableZ + exPosZ + width).color(255, 0, 0, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + posX + width, te.movableY + width, te.movableZ + exPosZ + posZ + width).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + posX + width, te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + width, posY + te.movableY + width, te.movableZ + exPosZ + posZ + width).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + width, posY + te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right start

            bufferbuilder.pos(te.movableX + exPosX + posX + width, posY + te.movableY + width, te.movableZ + exPosZ + posZ + width).color(192,192,192, 255).endVertex(); // right end
            bufferbuilder.pos(te.movableX + exPosX + posX + width, posY + te.movableY + width, te.movableZ + exPosZ + width).color(192,192,192, 255).endVertex(); // right start


            tessellator.draw();

            GlStateManager.popMatrix();
            // clean up GL crap
            GL11.glColor4f(1, 1, 1, 1);
            GlStateManager.enableLighting();

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        if(te.texture != null) {
            // setup GL crap
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            // make sure we are at brightest lighting possible
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 175, 240);

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("kypjci:textures/blocks/builder/" + te.texture + ".png"));

            GlStateManager.pushMatrix();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            GlStateManager.translate(x, 0, z);
            GlStateManager.rotate(0, 0, 1, 0);
            GlStateManager.translate(-x, 0, -z);
            bufferbuilder.pos(x + 1.0001, y - 0, z + 1).tex(1, 1).endVertex(); // Bottom left
            bufferbuilder.pos(x + 1.0001, y - 0, z + 1 - 1).tex(0, 1).endVertex(); // Bottom right

            bufferbuilder.pos(x + 1.0001, y + 1, z + 1 - 1).tex(0, 0).endVertex(); // Top right
            bufferbuilder.pos(x + 1.0001, y + 1, z + 1).tex(1, 0).endVertex(); // Top left
            tessellator.draw();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            GlStateManager.translate(x, 0, z);
            GlStateManager.rotate(90f, 0, 1, 0);
            GlStateManager.translate(-x, 0, -z);

            bufferbuilder.pos(x + 0.0001, y - 0, z + 1).tex(1, 1).endVertex(); // Bottom left
            bufferbuilder.pos(x + 0.0001, y - 0, z + 1 - 1).tex(0, 1).endVertex(); // Bottom right

            bufferbuilder.pos(x + 0.0001, y + 1, z + 1 - 1).tex(0, 0).endVertex(); // Top right
            bufferbuilder.pos(x + 0.0001, y + 1, z + 1).tex(1, 0).endVertex(); // Top left
            tessellator.draw();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            GlStateManager.translate(x, 0, z);
            GlStateManager.rotate(270f, 0, 1, 0);
            GlStateManager.translate(-x, 0, -z);

            bufferbuilder.pos(x + 1.0001, y - 0, z + 0).tex(1, 1).endVertex(); // Bottom left
            bufferbuilder.pos(x + 1.0001, y - 0, z + 0 - 1).tex(0, 1).endVertex(); // Bottom right

            bufferbuilder.pos(x + 1.0001, y + 1, z + 0 - 1).tex(0, 0).endVertex(); // Top right
            bufferbuilder.pos(x + 1.0001, y + 1, z + 0).tex(1, 0).endVertex(); // Top left
            tessellator.draw();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            GlStateManager.translate(x, 0, z);
            GlStateManager.rotate(180f, 0, 1, 0);
            GlStateManager.translate(-x, 0, -z);

            bufferbuilder.pos(x + 0.0001, y - 0, z + 0).tex(1, 1).endVertex(); // Bottom left
            bufferbuilder.pos(x + 0.0001, y - 0, z + 0 - 1).tex(0, 1).endVertex(); // Bottom right

            bufferbuilder.pos(x + 0.0001, y + 1, z + 0 - 1).tex(0, 0).endVertex(); // Top right
            bufferbuilder.pos(x + 0.0001, y + 1, z + 0).tex(1, 0).endVertex(); // Top left
            tessellator.draw();
            GlStateManager.popMatrix();

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("kypjci:textures/blocks/builder/floor_" + te.texture + ".png"));
            GlStateManager.pushMatrix();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            GlStateManager.translate(x, 0, z);
            GlStateManager.rotate(180f, 0, 1, 0);
            GlStateManager.translate(-x, 0, -z);

            bufferbuilder.pos(x + 0, y + 0.3751, z + 0).tex(1, 1).endVertex(); // Bottom left
            bufferbuilder.pos(x + 0, y + 0.3751, z + 0 - 1).tex(0, 1).endVertex(); // Bottom right

            bufferbuilder.pos(x - 1, y + 0.3751, z + 0 - 1).tex(0, 0).endVertex(); // Top right
            bufferbuilder.pos(x - 1, y + 0.3751, z + 0).tex(1, 0).endVertex(); // Top left
            tessellator.draw();
            GlStateManager.popMatrix();

        }

        // clean up GL crap
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        if(te.needItem != null) {
            EntityItem entityItem0 = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, te.needItem);
            entityItem0.hoverStart = 0F;
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x, y, z);
                GlStateManager.translate(0.5, 1.25, 0.5);
                GlStateManager.rotate((te.getWorld().getTotalWorldTime()+ partialTicks) * 3f, 0f, 1f, 0f);
                Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem0, 0, -1,0, 0F, 0F, false);
            }
            GlStateManager.popMatrix();
            GL11.glColor4f(1, 1, 1, 1);
        }

    }

    @Override
    public boolean isGlobalRenderer(TileEntityBuilder te) {
        return true;
    }
}
