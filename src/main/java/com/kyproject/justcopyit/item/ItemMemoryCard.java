package com.kyproject.justcopyit.item;

import com.google.gson.Gson;
import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.ModBlocks;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemMemoryCard extends ItemBase {

    public ItemMemoryCard(String name) {
        super(name);
        setMaxStackSize(64);
    }

    private static class BlockPlace {
        String type;
        String name;
        EnumFacing facing;
        ArrayList<BlockState> blocks;

        public BlockPlace(String type, String name, EnumFacing facing, ArrayList<BlockState> blocks) {
            this.type = type;
            this.name = name;
            this.facing = facing;
            this.blocks = blocks;
        }

        private static class BlockState {
            int x;
            int y;
            int z;
            int meta;
            String block;

            private BlockState(int x, int y, int z, int meta, String block) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.meta = meta;
                this.block = block;
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("type"))
        {
            if(Objects.equals(stack.getTagCompound().getString("type"), "card")) {
                tooltip.add("Blocks: " + stack.getTagCompound().getTagList("blocks", Constants.NBT.TAG_COMPOUND).tagCount());
            } else {
                    if(this.readNBTFile(stack.getTagCompound().getString("name")) != null) {
                        NBTTagCompound nbt = this.readNBTFile(stack.getTagCompound().getString("name"));
                        tooltip.add("Name: " + nbt.getString("name"));

                        tooltip.add("Blocks: " + nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND).tagCount());

                    } else {
                        NBTTagCompound nbt = stack.getTagCompound();
                        tooltip.add(nbt.getString("name"));
                    }
            }
        } else {
            tooltip.add("Empty");
        }
    }

    private NBTTagCompound readNBTFile(String fileName) {
        try {
            if(!Minecraft.getMinecraft().world.isRemote) {
                return CompressedStreamTools.read(new File("resources\\JustCopyIt\\structures\\" + fileName + ".dat"));
            } else {
                return CompressedStreamTools.read(new File("resources\\JustCopyIt\\structures\\" + fileName + ".dat"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.getBlockState(pos).getBlock() == ModBlocks.STRUCTURE_BUILDER) {

        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
