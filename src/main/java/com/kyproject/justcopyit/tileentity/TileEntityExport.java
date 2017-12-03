package com.kyproject.justcopyit.tileentity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
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

    ItemStackHandler inventory = new ItemStackHandler(1);
    boolean blockIsBuilding = false;
    int countBlocks = 0;
    int counter = 0;
    public EnumFacing facing;
    public String state = "Idle";


    public void buttonPressed(int id, String name) {
        if(id == 0) {
            this.createStructure(name);
        } else if(id == 1) {
            //this.startStructure();
        }
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

        ArrayList<BlockPlace.BlockState> blocks = new ArrayList<>();

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
            //if (inventory.getStackInSlot(0) != ItemStack.EMPTY) {

                // Creating the structure arrayList
                for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                    for (int z = this.rangeCalculator(rangeZ)[0]; z < this.rangeCalculator(rangeZ)[1]; z++) {
                        for (int y = this.rangeCalculator(rangeY)[0]; y < this.rangeCalculator(rangeY)[1]; y++) {
                            if (!world.isAirBlock(pos.add(x + fX, y, z + fZ))) {
                                IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                blocks.add(new BlockPlace.BlockState(x + fX, y, z + fZ, state.getBlock().getMetaFromState(state), state.getBlock().getRegistryName().toString()));
                            }
                        }
                    }
                }

                this.facing = EnumFacing.getFront(this.getBlockMetadata());
                BlockPlace structure = new BlockPlace("file", name, facing, blocks);

                this.createJson(name, structure);

        } else {
            this.setState("Error: no area found");
        }
    }

    private IBlockState getState() {
        return world.getBlockState(pos);
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

    private void createJson(String name, BlockPlace structure) {
        File file = new File("resources\\JustCopyIt\\structures\\" + name + ".json");
        if(!file.exists()) {
            try(Writer writer = new FileWriter("resources\\JustCopyIt\\structures\\" + name + ".json"))  {
                Gson gson = new GsonBuilder().create();
                gson.toJson(structure, writer);
                setState("Finished");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setState("File already exist");
        }
    }

    public String getSate() {
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
