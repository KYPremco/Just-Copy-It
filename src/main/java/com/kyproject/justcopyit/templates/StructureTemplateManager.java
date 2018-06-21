package com.kyproject.justcopyit.templates;

import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class StructureTemplateManager {
    private World world;

    public StructureTemplateManager(World world) {
        this.world = world;
    }

    public void placeBlockInWorld(BlockPos blockPos, IBlockState stateBlock, @Nullable NBTTagCompound nbt) {
        if(!world.isRemote) {
            if (stateBlock instanceof ItemSeeds) {
                world.setBlockState(blockPos, stateBlock.getBlock().getDefaultState(), 3);
            } else if(stateBlock.getBlock().getItem(world, new BlockPos(0,0,0), stateBlock).getItem() instanceof ItemDoor) {
                ItemDoor.placeDoor(world, blockPos, stateBlock.getValue(BlockHorizontal.FACING), stateBlock.getBlock(), false);
            } else {
                world.setBlockState(blockPos, stateBlock, 3);
                TileEntity tileEntity = world.getTileEntity(blockPos);
                if (tileEntity != null && nbt != null) {
                    nbt.setInteger("x", blockPos.getX());
                    nbt.setInteger("y", blockPos.getY());
                    nbt.setInteger("z", blockPos.getZ());
                    tileEntity.readFromNBT(nbt);
                }
            }

        }
    }

    public ItemStack getItem(IBlockState blockState) {
        Block block = blockState.getBlock();

        if(block.getDefaultState().getMaterial().isLiquid()) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
        }
        return block.getItem(world, new BlockPos(0,0,0), blockState);
    }

    public void removeItemFromContainer(ItemStackHandler inventory, int slot, boolean isLiquid) {
        if(!world.isRemote) {
            if (!isLiquid) {
                inventory.extractItem(slot, 1, false);
            } else {
                inventory.setStackInSlot(slot, new ItemStack(Items.BUCKET));
            }
        }
    }

    public String createNBTFile(String name, NBTTagCompound structure) {
        File file = new File("resources\\JustCopyIt\\structures\\" + name + ".dat");

        if(!file.exists()) {
            try {
                CompressedStreamTools.safeWrite(structure, file);
                return "Finished";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "File already exist";
        }

        return "Something went wrong!";
    }

    public NBTTagCompound readNBTFile(String fileName) {
        return this.getNBTFile(fileName);
    }

    private NBTTagCompound getNBTFile(String fileName) {
        try {
            return CompressedStreamTools.read(new File("resources\\JustCopyIt\\structures\\" + fileName + ".dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateNewStructureName() {
        int Duplicates = 0;
        File file = new File("resources\\JustCopyIt\\structures\\" + world.getTotalWorldTime() + "_" + Duplicates + ".dat");

        while(file.exists()) {
            Duplicates++;
            File newFile = new File("resources\\JustCopyIt\\structures\\" + world.getTotalWorldTime() + "_" + Duplicates + ".dat");

            if(!newFile.exists()) {
                break;
            }
        }

        return world.getTotalWorldTime() + "_" + Duplicates;
    }

}
