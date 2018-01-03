package com.kyproject.justcopyit.tileentity;

import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityScanner extends TileEntity{

    private ItemStackHandler inventory = new ItemStackHandler(37);

    public void buttonPressed(int id) {
        switch (id) {
            case 0:
                this.createStructure();
                break;
            default:
                break;
        }
    }

    private void createStructure() {
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
            System.out.println(inventory.getStackInSlot(0));
            if (!inventory.getStackInSlot(0).isEmpty()) {

                // Creating the structure arrayList
                for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                    for (int z = this.rangeCalculator(rangeZ)[0]; z < this.rangeCalculator(rangeZ)[1]; z++) {
                        for (int y = this.rangeCalculator(rangeY)[0]; y < this.rangeCalculator(rangeY)[1]; y++) {
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

                structureTemplate.create("card", "card", forward, -1, rangeX, rangeY, rangeZ);

                StructureTemplate.BlockPlace structure = structureTemplate.getStructure();

                //Saving the data to the memory card inside the slot
                if (inventory.getStackInSlot(0).getItem().equals(ModItems.MEMORY_CARD)) {
                    NBTTagCompound nbt;
                    ItemStack stack = inventory.getStackInSlot(0);

                    // Check if stack has NBT
                    if (stack.hasTagCompound()) {
                        nbt = stack.getTagCompound();
                    } else {
                        nbt = new NBTTagCompound();
                    }

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

                    // Saving all the data
                    stack.setTagCompound(nbt);
                    markDirty();
                }
            }
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

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
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