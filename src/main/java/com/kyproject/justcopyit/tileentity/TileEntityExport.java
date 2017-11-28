package com.kyproject.justcopyit.tileentity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Objects;

public class TileEntityExport extends TileEntity implements ITickable {

    public ArrayList<BlockPlace> blockStructure = new ArrayList<>();

    ItemStackHandler inventory = new ItemStackHandler(55);
    boolean blockIsBuilding = false;
    int countBlocks = 0;
    int counter = 0;
    public EnumFacing facing;

    private static class BlockPlace {
        private int x;
        private int y;
        private int z;
        private IBlockState state;
        private int rotate;
        private EnumFacing facing;

        private BlockPlace(int x, int y, int z, IBlockState state, int rotate, EnumFacing facing) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
            this.rotate = rotate;
            this.facing = facing;
        }
    }

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
        ArrayList<BlockPlace2.BlockState> blocks = new ArrayList<>();

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
            if (inventory.getStackInSlot(0) != ItemStack.EMPTY) {

                // Creating the structure arrayList
                for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                    for (int z = this.rangeCalculator(rangeZ)[0]; z < this.rangeCalculator(rangeZ)[1]; z++) {
                        for (int y = this.rangeCalculator(rangeY)[0]; y < this.rangeCalculator(rangeY)[1]; y++) {
                            if (!world.isAirBlock(pos.add(x + fX, y, z + fZ))) {
                                IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                blocks.add(new BlockPlace2.BlockState(x + fX, y, z + fZ, this.getBlockMetadata(), state.getBlock().getRegistryName().toString()));
                            }
                        }
                    }
                }

                BlockPlace2 structure = new BlockPlace2("file", name, facing, blocks);

                this.createJson(name, structure);


                // Saving the data to the memory card inside the slot
//                if (inventory.getStackInSlot(0).getItem() == ModItems.MEMORY_CARD) {
//                    NBTTagCompound nbt;
//                    ItemStack stack = inventory.getStackInSlot(0);
//
//                    // Check if stack has NBT
//                    if (stack.hasTagCompound()) {
//                        nbt = stack.getTagCompound();
//                    } else {
//                        nbt = new NBTTagCompound();
//                    }
//
//                    // Creating structure list
//                    NBTTagList tagList = new NBTTagList();
//                    for (int i = 0; i < blocks.size(); i++) {
//                        if (blocks.get(i) != null) {
//                            NBTTagCompound tag = new NBTTagCompound();
//                            tag.setString("facing", blocks.get(i).facing.toString());
//                            tag.setInteger("rotate", blocks.get(i).rotate);
//                            tag.setInteger("blockX" + i, blocks.get(i).x);
//                            tag.setInteger("blockY" + i, blocks.get(i).y);
//                            tag.setInteger("blockZ" + i, blocks.get(i).z);
//
//                            NBTUtil.writeBlockState(tag, blocks.get(i).state);
//                            tagList.appendTag(tag);
//                        }
//                    }
//                    nbt.setTag("structureList", tagList);
//
//                    // Saving all the data
//                    stack.setTagCompound(nbt);
//                }
            }
        }
    }

    private ArrayList<BlockPlace> getStructure() {

        ArrayList<BlockPlace> structureList = new ArrayList<>();

        // Reading
        if(inventory.getStackInSlot(0) != ItemStack.EMPTY) {
            if(inventory.getStackInSlot(0).getItem() == ModItems.MEMORY_CARD) {
                if(inventory.getStackInSlot(0).hasTagCompound()) {
                    if(inventory.getStackInSlot(0).getTagCompound().hasKey("structureList")) {

                        EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());

                        NBTTagList tagList = inventory.getStackInSlot(0).getTagCompound().getTagList("structureList", Constants.NBT.TAG_COMPOUND);
                        for(int i=0;i < tagList.tagCount();i++) {
                            NBTTagCompound tag = tagList.getCompoundTagAt(i);
                            int x = tag.getInteger("blockX" + i);
                            int z = tag.getInteger("blockZ" + i);
                            int y = tag.getInteger("blockY" + i);

                            int[] newBlockPosXYZ;
                            newBlockPosXYZ = this.getRotateStructure(EnumFacing.byName(tag.getString("facing")), forward, x, y, z);

                            IBlockState state = NBTUtil.readBlockState(tag);
                            structureList.add(new BlockPlace(newBlockPosXYZ[0], newBlockPosXYZ[1], newBlockPosXYZ[2],state.withRotation(getRotationBlock(EnumFacing.byName(tag.getString("facing")), forward)),this.getBlockMetadata(), forward));
                        }
                    }
                }
            }
        }

        return structureList;

    }

    private Rotation getRotationBlock(EnumFacing origin, EnumFacing newRotation) {
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

    private int[] getRotateStructure(EnumFacing origin, EnumFacing newRotation, int oldX, int oldY, int oldZ) {
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

    private void startStructure() {
        blockStructure.clear();
        blockStructure = (ArrayList<BlockPlace>) getStructure().clone();
        blockIsBuilding = true;
        countBlocks = 0;
        counter = 0;
    }

    @Override
    public void update() {
        if(blockIsBuilding) {
            int tickCounter = 0;
            if(counter == tickCounter) {
                if(blockStructure.size() == 0) {
                    blockIsBuilding = false;
                    countBlocks = tickCounter;
                } else {
                    if(world.isAirBlock(new BlockPos(pos).add(blockStructure.get(0).x, blockStructure.get(0).y, blockStructure.get(0).z))) {
                        for (int slot = 0; slot < inventory.getSlots() - 1; slot++) {
                            if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                                if (inventory.getStackInSlot(slot) != ItemStack.EMPTY) {
                                    if(this.haveItem(blockStructure.get(0).state, slot)) {
                                        inventory.extractItem(slot, 1, false);
                                        world.setBlockState(new BlockPos(pos).add(blockStructure.get(0).x, blockStructure.get(0).y, blockStructure.get(0).z), blockStructure.get(0).state);
                                        blockStructure.remove(0);
                                        break;
                                    }
                                }

                            }
                        }
                    } else {
                        blockStructure.remove(0);
                    }
                    countBlocks++;
                }
                counter = 0;
            } else {
                counter++;
            }
        }
    }

    private static class BlockPlace2 {
        String type;
        String name;
        EnumFacing facing;
        ArrayList<BlockState> blocks;

        public BlockPlace2(String type, String name, EnumFacing facing, ArrayList<BlockState> blocks) {
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

    private void createJson(String name, BlockPlace2 structure) {
        try (Writer writer = new FileWriter("resources\\JustCopyIt\\structures\\" + name + ".json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(structure, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean haveItem(IBlockState block, int slot) {
        int metaFromState = block.getBlock().getMetaFromState(block);

        return Objects.equals(inventory.getStackInSlot(slot).getItem().getRegistryName(), block.getBlock().getRegistryName());
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
