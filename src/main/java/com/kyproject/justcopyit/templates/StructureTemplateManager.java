package com.kyproject.justcopyit.templates;

import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
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

public class StructureTemplateManager {
    private World world;

    public StructureTemplateManager(World world) {
        this.world = world;
    }

    public void placeBlockInWorld(BlockPos blockPos, IBlockState stateBlock, @Nullable NBTTagCompound nbt) {
        if(!world.isRemote) {
            System.out.println(blockPos);
            if (stateBlock instanceof ItemSeeds) {
                world.setBlockState(blockPos, stateBlock.getBlock().getDefaultState(), 3);
            } else if(stateBlock.getBlock().getItem(world, null, stateBlock).getItem() instanceof ItemDoor) {
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

        return block.getItem(world, null, blockState);
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

}
