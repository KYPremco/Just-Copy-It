package com.kyproject.justcopyit.block.render;

import com.kyproject.justcopyit.init.ModBlocks;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderAreaLine extends TileEntitySpecialRenderer<TileEntityWorldMarker> {

    private static final EntityItem ITEM = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, new ItemStack(Blocks.COBBLESTONE));

    @Override
    public void render(TileEntityWorldMarker te, double posX, double posY, double posZ, float partialTicks, int destroyStage, float alpha) {
        super.render(te, posX, posY, posZ, partialTicks, destroyStage, alpha);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.translate(posX, posY, posZ);
        Color color = new Color(255, 255, 0, 50);
        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glLineWidth(3F);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        double x = te.rangeX;
        double y = te.rangeY;
        double z = te.rangeZ;
        double width = 0.5;

        if(x == 0 || y == 0 || z == 0) {
            if(z != 0) {
                buffer.pos(width, width, z + width).color(255, 10, 10, 255).endVertex(); // right end
                buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right start
            }
            if(y != 0) {
                buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right end
                buffer.pos(width, y + width, width).color(255, 10, 10, 255).endVertex(); // right end
            }
            if(x != 0) {
                buffer.pos(x + width, width, width).color(255, 10, 10, 255).endVertex(); // right end
                buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right start
            }
        } else {


            //First block
            buffer.pos(x + width, width, width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(x + width, y + width, width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, y  + width, width).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(x  + width, width, width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(x  + width, y  + width, width).color(255, 10, 10, 255).endVertex(); // right end

            buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, y + width, width).color(255, 10, 10, 255).endVertex(); // right end

            //Second block
            buffer.pos(x  + width, width, width + z).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(x  + width, y  + width, width + z).color(255, 10, 10, 255).endVertex(); // right end

            buffer.pos(width, width, width + z).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, y  + width, width + z).color(255, 10, 10, 255).endVertex(); // right end

            buffer.pos(x + width, width, width + z).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, width, width + z).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(x + width, y + width, width + z).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, y  + width, width + z).color(255, 10, 10, 255).endVertex(); // right start

            //Connector block
            buffer.pos(width, width, z + width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, width, width).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(x + width, width, z + width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(x + width, width, width).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(width, y + width, z + width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(width, y + width, width).color(255, 10, 10, 255).endVertex(); // right start

            buffer.pos(x + width, y + width, z + width).color(255, 10, 10, 255).endVertex(); // right end
            buffer.pos(x + width, y + width, width).color(255, 10, 10, 255).endVertex(); // right start

        }
        tessellator.draw();

        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }


    @Override
    public boolean isGlobalRenderer(TileEntityWorldMarker te) {
        return true;
    }
}
