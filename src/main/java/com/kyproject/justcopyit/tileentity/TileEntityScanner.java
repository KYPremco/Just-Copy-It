package com.kyproject.justcopyit.tileentity;

import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.templates.StructureTemplateManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityScanner extends TileEntity implements ITickable{

    private ItemStackHandler inventory = new ItemStackHandler(37);

    private StructureTemplate structureTemplate;
    private boolean isScanning = false;
    private boolean isOP = false;
    private int counter = 0;
    private int yValue = 0;
    private int zValueStart = 0;
    private int maxRows = 0;

    public void buttonPressed(int id, boolean op) {
        switch (id) {
            case 0:
                this.startScanner(op);
                break;
            default:
                break;
        }
    }

    private void startScanner(boolean op) {
        structureTemplate = new StructureTemplate();
        isScanning = true;
        counter = 0;
        maxRows = 0;
        zValueStart = 0;
        yValue = 0;
        isOP = op;

    }

    private void calculateMaxRows(int z, int x) {
        double square = Math.sqrt(Math.abs(z) * Math.abs(x));
        double max = 8000D / square;


        maxRows = (int) Math.ceil(max);
    }

    private void scanningStructure(boolean op) {
        StructureTemplateManager structureTemplateManager = new StructureTemplateManager(world);

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
            if (!inventory.getStackInSlot(0).isEmpty()) {
                if(maxRows == 0) {
                    this.calculateMaxRows(rangeZ, rangeX);
                    yValue = this.rangeCalculator(rangeY)[0];
                    zValueStart = this.rangeCalculator(rangeZ)[0];
                }

                // Creating the structure arrayList
                if(yValue < this.rangeCalculator(rangeY)[1]) {
                    for (int z = zValueStart; z < zValueStart + maxRows; z++) {
                        if(z >= this.rangeCalculator(rangeZ)[1]) {
                            break;
                        }
                        for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                            if (!world.isAirBlock(pos.add(x + fX, yValue, z + fZ))) {
                                if (world.getBlockState(pos.add(x + fX, yValue, z + fZ)).getMaterial().isLiquid()) {
                                    if (world.getBlockState(pos.add(x + fX, yValue, z + fZ)).getBlock().getMetaFromState(world.getBlockState(pos.add(x + fX, yValue, z + fZ)).getActualState(world, pos.add(x + fX, yValue, z + fZ))) == 0) {
                                        IBlockState state = world.getBlockState(pos.add(x + fX, yValue, z + fZ)).getActualState(world, pos.add(x + fX, yValue, z + fZ));
                                        structureTemplate.addLayer("liquid", x + fX, yValue, z + fZ, state, world, pos);
                                    }
                                } else {
                                    IBlockState state = world.getBlockState(pos.add(x + fX, yValue, z + fZ)).getActualState(world, pos.add(x + fX, yValue, z + fZ));
                                    structureTemplate.addLayer("blockLayer", x + fX, yValue, z + fZ, state, world, pos);
                                }
                            }
                        }
                    }
                    yValue++;
                    counter = 0;
                } else {
                    structureTemplate.combine();

                    structureTemplate.create("card", "card", forward, -1, rangeX, rangeY, rangeZ);

                    StructureTemplate.BlockPlace structure = structureTemplate.getStructure();

                    System.out.println(structure.blocks.size());

                    isScanning = false;

                    //Saving the data to the memory card inside the slot
                    if (inventory.getStackInSlot(0).getItem().equals(ModItems.BLUEPRINT) || (inventory.getStackInSlot(0).getItem().equals(ModItems.BLUEPRINT_CREATIVE) && op)) {
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

                        if (!inventory.getStackInSlot(0).isEmpty()) {
                            nbt.setTag("blocks", tagList);
                            nbt.setString("type", structure.type);
                            nbt.setString("name", structureTemplateManager.generateNewStructureName());
                            nbt.setInteger("durability", structure.durability);
                            nbt.setInteger("rangeX", rangeX);
                            nbt.setInteger("rangeY", rangeY);
                            nbt.setInteger("rangeZ", rangeZ);
                            nbt.setString("facing", structure.facing.toString());

                            // Creating NBT file on server
                            structureTemplateManager.createNBTFile(structureTemplateManager.generateNewStructureName(), nbt);

                            // Clears blocks, create memory card NBT
                            nbt.setInteger("blockCount", tagList.tagCount());
                            nbt.removeTag("blocks");
                            stack.setTagCompound(nbt);

                            // Testing item NBT
                            //structureTemplateManager.createNBTFile(structureTemplateManager.generateNewStructureName(), nbt);
                        }

                        markDirty();
                    }
                    markDirty();
                }
            } else {
                isScanning = false;
            }
        }
    }

    @Override
    public void update() {
        if(isScanning) {
            if(counter == 1) {
                scanningStructure(isOP);
                counter++;
            } else {
                counter++;
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