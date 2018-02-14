package com.kyproject.justcopyit.tileentity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kyproject.justcopyit.templates.StructureTemplate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;

public class TileEntityExport extends TileEntity {

    private ItemStackHandler inventory = new ItemStackHandler(1);
    private int durability = 0;
    private String state = "Idle";

    public void buttonPressed(int id, String name) {
        switch (id) {
            case 0:
                this.createStructure(name);
                break;
            case 1:
                durability++;
                break;
            case 2:
                if(durability > 0) {
                    durability--;
                }
                break;
            default:
                break;
        }
    }

    public int getDurability() {
        return this.durability;
    }

    private int[] rangeCalculator(int range) {
        int startRange;
        int endRange;

        if(range < 0) {
            startRange = range + 1;
            endRange = 0;
        } else {
            startRange = 1;
            endRange = range;
        }

        int[] rangeCalculator = new int[2];
        rangeCalculator[0] = startRange;
        rangeCalculator[1] = endRange;

        return rangeCalculator;
    }

    private void createStructure(String name) {
        this.setState("Creating...");

        StructureTemplate structureTemplate = new StructureTemplate();

        EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());
        int fX = forward.getFrontOffsetX();
        int fZ = forward.getFrontOffsetZ();
        TileEntityWorldMarker te;

        int rangeX = 0;
        int rangeY = 0;
        int rangeZ = 0;

        if(fX != 0) {
            te = (TileEntityWorldMarker) world.getTileEntity(pos.add(fX,0,0));
        } else {
            te = (TileEntityWorldMarker) world.getTileEntity(pos.add(0,0,fZ));
        }

        if(te != null) {
            rangeX = te.rangeX;
            rangeY = te.rangeY;
            rangeZ = te.rangeZ;
        }

        // if nothing is in slot just skip it! and range is all filled
        if(rangeX  != 0 && rangeY != 0 && rangeZ != 0) {
            // Creating the structure arrayList
            for (int y = this.rangeCalculator(rangeY)[0]; y < this.rangeCalculator(rangeY)[1]; y++) {
                for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                    for (int z = this.rangeCalculator(rangeZ)[0]; z < this.rangeCalculator(rangeZ)[1]; z++) {
                        if (!world.isAirBlock(pos.add(x + fX, y, z + fZ))) {
                            if (world.getBlockState(pos.add(x + fX, y, z + fZ)).getMaterial().isLiquid()) {
                                if (world.getBlockState(pos.add(x + fX, y, z + fZ)).getBlock().getMetaFromState(world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ))) == 0) {
                                    IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                    structureTemplate.addLayer("liquid", x + fX, y, z + fZ, state, world, pos);
                                }
                            } else {
                                IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                structureTemplate.addLayer("blockLayer", x + fX, y, z + fZ, state, world, pos);
                            }
                        }
                    }
                }
            }
            structureTemplate.combine();

            // durability  -1 = infinity
            if(durability == 0) {
                structureTemplate.create("file", name, forward, -1, rangeX, rangeY, rangeZ);
            } else {
                structureTemplate.create("file", name, forward, durability, rangeX, rangeY, rangeZ);
            }
            StructureTemplate.BlockPlace structure = structureTemplate.getStructure();
            NBTTagCompound nbt = new NBTTagCompound();

            // Creating structure list
            NBTTagList tagList = new NBTTagList();
            for (int i = 0; i < structure.blocks.size(); i++) {
                if (structure.blocks.get(i) != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setInteger("blockX" + i, structure.blocks.get(i).x);
                    tag.setInteger("blockY" + i, structure.blocks.get(i).y);
                    tag.setInteger("blockZ" + i, structure.blocks.get(i).z);
                    NBTUtil.writeBlockState(tag, structure.blocks.get(i).state);
                    if(structure.blocks.get(i).nbtTagCompound != null) {
                        tag.setTag("nbt", structure.blocks.get(i).nbtTagCompound);
                    }
                    tagList.appendTag(tag);
                }
            }
            nbt.setTag("blocks", tagList);
            nbt.setString("type", structure.type);
            nbt.setString("name", structure.name);
            nbt.setInteger("durability", structure.durability);
            nbt.setInteger("rangeX", rangeX);
            nbt.setInteger("rangeY", rangeY);
            nbt.setInteger("rangeZ", rangeZ);
            nbt.setString("facing", structure.facing.toString());

            this.setState(structureTemplate.createNBTFile(name, nbt));

        } else {
            this.setState("Error: no area found");
        }
    }

    private IBlockState getState() {
        return world.getBlockState(pos);
    }

    public String getStateExport() {
        return this.state;
    }

    private void setState(String state) {
        this.state = state;
        this.sendUpdates();
    }

    private void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
        world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
        markDirty();
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    //Some other stuff
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.state = compound.getString("state");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setString("state", this.state);
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)inventory : super.getCapability(capability, facing);
    }
}