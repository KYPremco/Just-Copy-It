package com.kyproject.justcopyit.tileentity;
import com.google.gson.Gson;
import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class TileEntityBuilder extends TileEntity implements ITickable {

    BlockPlace blockStructure;

    ItemStackHandler inventory = new ItemStackHandler(55);
    boolean blockIsBuilding = false;
    int countBlocks = 0;
    int counter = 0;

    public void buttonPressed(int id) {
        if(id == 0) {
            this.createStructure();
        } else if(id == 1) {
            this.startStructure();
        }
    }

    public void startStructure() {
        this.blockStructure = this.getStructure();

        blockIsBuilding = true;
        countBlocks = 0;
        counter = 0;
    }

    public void createStructure() {
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
            if (inventory.getStackInSlot(54) != ItemStack.EMPTY) {

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
                BlockPlace structure = new BlockPlace("card", "card", forward, blocks);



                //Saving the data to the memory card inside the slot
                if (inventory.getStackInSlot(54).getItem() == ModItems.MEMORY_CARD) {
                    NBTTagCompound nbt;
                    ItemStack stack = inventory.getStackInSlot(54);

                    // Check if stack has NBT
                    if (stack.hasTagCompound()) {
                        nbt = stack.getTagCompound();
                    } else {
                        nbt = new NBTTagCompound();
                    }

                    // Creating structure list
                    NBTTagList tagList = new NBTTagList();
                    for (int i = 0; i < blocks.size(); i++) {
                        if (blocks.get(i) != null) {
                            NBTTagCompound tag = new NBTTagCompound();
                            tag.setInteger("blockX" + i, structure.blocks.get(i).x);
                            tag.setInteger("blockY" + i, structure.blocks.get(i).y);
                            tag.setInteger("blockZ" + i, structure.blocks.get(i).z);

                            tag.setInteger("meta" + i, structure.blocks.get(i).meta);
                            tag.setString("block" + i, structure.blocks.get(i).block);
                            tagList.appendTag(tag);
                        }
                    }
                    nbt.setTag("blocks", tagList);
                    nbt.setString("type", structure.type);
                    nbt.setString("name", structure.name);
                    nbt.setString("facing", structure.facing.toString());

                    // Saving all the data
                    stack.setTagCompound(nbt);
                }
            }
        }
    }

    public static class BlockPlace {
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

        public static class BlockState {
            int x;
            int y;
            int z;
            int meta;
            String block;

            public BlockState(int x, int y, int z, int meta, String block) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.meta = meta;
                this.block = block;
            }
        }
    }

    public BlockPlace getStructure() {

        ArrayList<BlockPlace.BlockState> blockList = new ArrayList<>();

        // Reading
        if(inventory.getStackInSlot(54) != ItemStack.EMPTY) {
            if(inventory.getStackInSlot(54).getItem() == ModItems.MEMORY_CARD) {
                if(inventory.getStackInSlot(54).hasTagCompound()) {
                    if(inventory.getStackInSlot(54).getTagCompound().hasKey("type")) {
                        if(Objects.equals(inventory.getStackInSlot(54).getTagCompound().getString("type"), "card")) {
                            EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());

                            NBTTagList tagList = inventory.getStackInSlot(54).getTagCompound().getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                            NBTTagCompound nbt = inventory.getStackInSlot(54).getTagCompound();
                            for(int i=0;i < tagList.tagCount();i++) {
                                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                                int x = tag.getInteger("blockX" + i);
                                int z = tag.getInteger("blockZ" + i);
                                int y = tag.getInteger("blockY" + i);
                                int meta = tag.getInteger("meta" + i);
                                String block = tag.getString("block" + i);

                                int[] newBlockPosXYZ;
                                newBlockPosXYZ = this.getRotateStructure(EnumFacing.byName(nbt.getString("facing")), forward, x, y, z);

                                blockList.add(new BlockPlace.BlockState(newBlockPosXYZ[0], newBlockPosXYZ[1], newBlockPosXYZ[2], meta , block));
                            }
                            // state.withRotation(getRotationBlock(EnumFacing.byName(tag.getString("facing"))
                            return new BlockPlace(nbt.getString("type"), nbt.getString("name"), EnumFacing.byName(nbt.getString("facing")), blockList);
                        } else {
                            BlockPlace structure = readJson(inventory.getStackInSlot(54).getTagCompound().getString("name"));
                            EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());

                            for(int i=0;i < structure.blocks.size(); i++) {
                                int[] newBlockPosXYZ;
                                newBlockPosXYZ = this.getRotateStructure(structure.facing, forward, structure.blocks.get(i).x, structure.blocks.get(i).y, structure.blocks.get(i).z);

                                blockList.add(new BlockPlace.BlockState(newBlockPosXYZ[0], newBlockPosXYZ[1], newBlockPosXYZ[2], structure.blocks.get(i).meta , structure.blocks.get(i).block));
                            }

                            return new BlockPlace(structure.type, structure.name, structure.facing, blockList);
                        }
                    }
                }
            }
        }

        return new BlockPlace("card", "card", EnumFacing.NORTH, blockList);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void update() {
        if(blockIsBuilding) {
            int tickCounter = 0;
            if(counter == tickCounter) {
                if(blockStructure.blocks.size() == countBlocks) {
                    blockIsBuilding = false;
                    countBlocks = tickCounter;
                } else {
                    if(world.isAirBlock(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z))) {
                        for (int slot = 0; slot < inventory.getSlots() - 1; slot++) {
                            if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                                if (inventory.getStackInSlot(slot) != ItemStack.EMPTY) {
                                    if(this.haveItem(blockStructure.blocks.get(countBlocks).block, slot)) {
                                        inventory.extractItem(slot, 1, false);
                                        BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                                        world.setBlockState(blockPos, Block.getBlockFromName(blockStructure.blocks.get(countBlocks).block).getStateFromMeta(blockStructure.blocks.get(countBlocks).meta).withRotation(this.getRotationBlock(blockStructure.facing, EnumFacing.getFront(this.getBlockMetadata()))));
                                        countBlocks++;
                                        break;
                                    }
                                }

                            }
                        }
                    } else {
                        countBlocks++;
                    }
                }
                counter = 0;
            } else {
                counter++;
            }
        }
    }

    private BlockPlace readJson(String fileName) {
        try {
            return new Gson().fromJson(new FileReader("resources\\JustCopyIt\\structures\\" + fileName + ".json"), BlockPlace.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Rotation getRotationBlock(EnumFacing origin, EnumFacing newRotation) {
        if(origin.getAxis() != EnumFacing.Axis.Y) {
            if(origin.getAxis() == newRotation.getAxis()) {
                if(origin.getName().equals(newRotation.getName())) {
                    return Rotation.NONE;
                } else {
                    return Rotation.CLOCKWISE_180;
                }
            } else {
                if(origin.rotateY().getName().equals(newRotation.getName())){
                    return Rotation.CLOCKWISE_90;
                } else {
                    return Rotation.COUNTERCLOCKWISE_90;
                }
            }
        } else {
            return Rotation.NONE;
        }
    }

    public int[] getRotateStructure(EnumFacing origin, EnumFacing newRotation, int oldX, int oldY, int oldZ) {
        int x;
        int z;

        if(origin.getAxis() == newRotation.getAxis()) {
            if(origin.getName().equals(newRotation.getName())) {
                //0
                x = (oldX);
                z = (oldZ);
            } else {
                // 180
                x = (-oldX);
                z = (-oldZ);
            }
        } else {
            if(origin.rotateY().getName().equals(newRotation.getName())){
                //90
                x = (-oldZ);
                z = (oldX);
            } else {
                //270
                x = (oldZ);
                z = (-oldX);
            }
        }

        int[] posXYZ = new int[3];
        posXYZ[0] = x;
        posXYZ[1] = oldY;
        posXYZ[2] = z;

        return posXYZ;
    }


    public int[] rangeCalculator(int range) {
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

    public boolean haveItem(String block, int slot) {
        return Objects.equals(inventory.getStackInSlot(slot).getItem().getRegistryName(), Block.getBlockFromName(block).getRegistryName());
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 1000;
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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
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
