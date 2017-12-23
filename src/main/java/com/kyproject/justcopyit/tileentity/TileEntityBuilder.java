package com.kyproject.justcopyit.tileentity;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.Filters;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntityBuilder extends TileEntity implements ITickable {

    private StructureTemplate.BlockPlace blockStructure;
    public static ArrayList<Filters.changeItemFilter> filter = new ArrayList<>();

    private ItemStackHandler inventory = new ItemStackHandler(55);
    private boolean blockIsBuilding = false;
    private int countBlocks = 0;
    private int counter = 0;
    private boolean isLiquid = false;
    private boolean checked = false;
    private boolean removedDurability = false;

    public void buttonPressed(int id) {
        switch (id) {
            case 0:
                this.createStructure();
                break;
            case 1:
                this.startStructure();
                break;
            case 2:
                this.setChecked();
                break;
            default:
                break;
        }
    }

    private void setChecked () {
        this.checked = !this.checked;
        sendUpdates();
    }

    public boolean getChecked() {
        return this.checked;
    }

    private void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
        world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
        markDirty();
    }

    private IBlockState getState() {
        return world.getBlockState(pos);
    }

    private void startStructure() {
        this.blockStructure = this.getStructure();
        this.checkStructureBuild();
        this.removedDurability = false;
        this.countBlocks = 0;
        this.counter = 0;
        this.blockIsBuilding = true;
        sendUpdates();
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
            if (!inventory.getStackInSlot(54).isEmpty()) {
                // Creating the structure arrayList
                for (int x = this.rangeCalculator(rangeX)[0]; x < this.rangeCalculator(rangeX)[1]; x++) {
                    for (int z = this.rangeCalculator(rangeZ)[0]; z < this.rangeCalculator(rangeZ)[1]; z++) {
                        for (int y = this.rangeCalculator(rangeY)[0]; y < this.rangeCalculator(rangeY)[1]; y++) {
                            if (!world.isAirBlock(pos.add(x + fX, y, z + fZ))) {
                                if (world.getBlockState(pos.add(x + fX, y, z + fZ)).getMaterial().isLiquid()) {
                                    if (world.getBlockState(pos.add(x + fX, y, z + fZ)).getBlock().getMetaFromState(world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ))) == 0) {
                                        IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                        structureTemplate.addLayer("liquid", x + fX, y, z + fZ, state);
                                    }
                                } else {
                                    IBlockState state = world.getBlockState(pos.add(x + fX, y, z + fZ)).getActualState(world, pos.add(x + fX, y, z + fZ));
                                    structureTemplate.addLayer("blockLayer", x + fX, y, z + fZ, state);
                                }
                            }
                        }
                    }
                }
                structureTemplate.combine();

                structureTemplate.create("card", "card", forward, -1);
                StructureTemplate.BlockPlace structure = structureTemplate.getStructure();

                 //Saving the data to the memory card inside the slot
                if (inventory.getStackInSlot(54).getItem().equals(ModItems.MEMORY_CARD)) {
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
                    for (int i = 0; i < structure.blocks.size(); i++) {
                        if (structure.blocks.get(i) != null) {
                            NBTTagCompound tag = new NBTTagCompound();
                            tag.setInteger("blockX" + i, structure.blocks.get(i).x);
                            tag.setInteger("blockY" + i, structure.blocks.get(i).y);
                            tag.setInteger("blockZ" + i, structure.blocks.get(i).z);
                            NBTUtil.writeBlockState(tag, structure.blocks.get(i).state);
                            tagList.appendTag(tag);
                        }
                    }
                    nbt.setTag("blocks", tagList);
                    nbt.setString("type", structure.type);
                    nbt.setString("name", structure.name);
                    nbt.setInteger("durability", structure.durability);
                    nbt.setString("facing", structure.facing.toString());

                    // Saving all the data
                    stack.setTagCompound(nbt);

                    markDirty();
                }
            }
        }
    }

    private StructureTemplate.BlockPlace getStructure() {
        StructureTemplate structureTemplate = new StructureTemplate();

        // Reading
        if(inventory.getStackInSlot(54) != ItemStack.EMPTY) {
            if(inventory.getStackInSlot(54).getItem() == ModItems.MEMORY_CARD || inventory.getStackInSlot(54).getItem() == ModItems.MEMORY_CARD_CREATIVE) {
                if(inventory.getStackInSlot(54).hasTagCompound()) {
                    if(inventory.getStackInSlot(54).getTagCompound().hasKey("type")) {
                        EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());

                        NBTTagCompound nbt = inventory.getStackInSlot(54).getTagCompound();
                        NBTTagList tagList = nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                        for(int i=0;i < tagList.tagCount();i++) {
                            NBTTagCompound tag = tagList.getCompoundTagAt(i);
                            int x = tag.getInteger("blockX" + i);
                            int z = tag.getInteger("blockZ" + i);
                            int y = tag.getInteger("blockY" + i);
                            IBlockState state = NBTUtil.readBlockState(tag);

                            int[] newBlockPosXYZ;
                            newBlockPosXYZ = this.getRotateStructure(EnumFacing.byName(nbt.getString("facing")), forward, x, y, z);

                            structureTemplate.add(newBlockPosXYZ[0], newBlockPosXYZ[1], newBlockPosXYZ[2], state);
                        }

                        structureTemplate.create(nbt.getString("type"), nbt.getString("name"), EnumFacing.byName(nbt.getString("facing")), nbt.getInteger("durability"));
                        return structureTemplate.getStructure();
                    }
                }
            }
        }

        structureTemplate.create("card", "card", EnumFacing.NORTH, -1);
        return structureTemplate.getStructure();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void update() {
        if(!world.isRemote) {
            if (this.blockIsBuilding) {
                int tickCounter = 0;
                if (counter == tickCounter) {
                    if (blockStructure.blocks.size() == countBlocks) {
                        this.blockIsBuilding = false;
                        if (this.removedDurability) {
                            this.memorycardDurability();
                        }
                        countBlocks = blockStructure.blocks.size();
                    } else {
                        if (inventory.getStackInSlot(54).getItem().equals(ModItems.MEMORY_CARD_CREATIVE)) {
                            EnumFacing facing_new = EnumFacing.getFront(this.getBlockMetadata());
                            EnumFacing facing_original = blockStructure.facing;

                            for (int countBlocks = 0; countBlocks < blockStructure.blocks.size(); countBlocks++) {
                                BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                                IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                                stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);

                                world.setBlockState(blockPos, stateBlock);
                            }
                            this.memorycardDurability();
                            countBlocks = blockStructure.blocks.size();
                        } else {
                            boolean build;

                            if (!inventory.getStackInSlot(54).isEmpty()) {
                                if (world.isAirBlock(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z))) {
                                    build = true;
                                } else {
                                    if (world.getBlockState(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z)).getMaterial().isLiquid()) {
                                        build = true;
                                    } else {
                                        countBlocks++;
                                        build = false;

                                    }
                                }
                                if (!this.removedDurability) {
                                    this.removedDurability = true;
                                }
                                if (build) {
                                    boolean skipBlock = true;
                                    for (int slot = 0; slot < inventory.getSlots() - 1; slot++) {
                                        if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                                            if (inventory.getStackInSlot(slot) != ItemStack.EMPTY) {
                                                if (this.haveItem(blockStructure.blocks.get(countBlocks).state.getBlock().getRegistryName().toString(), slot)) {
                                                    BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                                                    IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                                                    EnumFacing facing_new = EnumFacing.getFront(this.getBlockMetadata());
                                                    EnumFacing facing_original = blockStructure.facing;
                                                    stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);

                                                    world.setBlockState(blockPos, stateBlock);

                                                    if (!this.isLiquid) {
                                                        inventory.extractItem(slot, 1, false);
                                                    } else {
                                                        inventory.setStackInSlot(slot, new ItemStack(Items.BUCKET));
                                                        this.updateWaterTick(blockPos);
                                                    }

                                                    this.isLiquid = false;
                                                    skipBlock = false;
                                                    countBlocks++;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (skipBlock && this.checked) {
                                        countBlocks++;
                                    }
                                }
                            } else {
                                this.blockIsBuilding = false;
                            }
                        }
                    }
                    counter = 0;
                } else {
                    counter++;
                }
            }
        }
    }

    private void checkStructureBuild() {
        List<BlockPos> blockPosition = new ArrayList<>();
        for(int block = 0; block < blockStructure.blocks.size();block++) {
            BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(block).x, blockStructure.blocks.get(block).y, blockStructure.blocks.get(block).z);
            IBlockState structureState = blockStructure.blocks.get(block).state;
            IBlockState currentState = world.getBlockState(blockPos).getActualState(world, blockPos);

            if(this.getStateWithRotation(blockStructure.facing, EnumFacing.getFront(this.getBlockMetadata()), structureState).equals(currentState)) {
                blockPosition.add(blockPos);
            }
        }
        for (BlockPos blocks : blockPosition) {
            for(int block = 0; block < blockStructure.blocks.size();block++) {
                BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(block).x, blockStructure.blocks.get(block).y, blockStructure.blocks.get(block).z);
                if(blockPos.equals(blocks)) {
                    blockStructure.blocks.remove(block);
                }
            }
        }
    }

    private void memorycardDurability() {
        if(blockStructure.durability == 1) {
            inventory.extractItem(54, 1, false);
        } else {
            NBTTagCompound nbt = inventory.getStackInSlot(54).getTagCompound();
            if(nbt != null) {
                if(nbt.hasKey("durability")) {
                    if(nbt.getInteger("durability") > 1) {
                        int durability = nbt.getInteger("durability");
                        nbt.setInteger("durability", --durability);
                        inventory.getStackInSlot(54).setTagCompound(nbt);
                    }
                }
            }
        }
    }

    public ArrayList<Filters.changeItemFilter> readJsonFilter() {
        try {
            Type type = new TypeToken<ArrayList<Filters.changeItemFilter>>(){}.getType();
            return new Gson().fromJson(new FileReader("resources\\JustCopyIt\\changeItemFilter.json"), type);
        } catch (IOException e) {
            JustCopyIt.logger.error(e);
        }
        return null;
    }

    private IBlockState getStateWithRotation(EnumFacing origin, EnumFacing newRotation, IBlockState state) {

        PropertyDirection allDirections = BlockDirectional.FACING;
        PropertyDirection horizontal = BlockHorizontal.FACING;
        PropertyDirection torchDirections = BlockTorch.FACING;

        if(state.getPropertyKeys().contains(allDirections)) {
            return state.withProperty(allDirections, this.setRotation(origin, newRotation, state.getValue(allDirections)));
        } else if (state.getPropertyKeys().contains(horizontal)) {
            return state.withProperty(horizontal, this.setRotation(origin, newRotation, state.getValue(horizontal)));
        } else if (state.getPropertyKeys().contains(torchDirections)) {
            return state.withProperty(torchDirections, this.setRotation(origin, newRotation, state.getValue(torchDirections)));
        }
        return state;
    }

    private EnumFacing setRotation(EnumFacing origin, EnumFacing newRotation, EnumFacing blockRotation) {
        if(blockRotation.getAxis() != EnumFacing.Axis.Y) {
            if(origin.getAxis() == newRotation.getAxis()) {
                if(origin.getName().equals(newRotation.getName())) {
                    return blockRotation;
                } else {
                    return blockRotation.getOpposite();
                }
            } else {
                if(origin.rotateY().getName().equals(newRotation.getName())){
                    return blockRotation.rotateY();
                } else {
                    return blockRotation.rotateYCCW();
                }
            }
        } else {
            return blockRotation;
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

    private void updateWaterTick(BlockPos blockPos) {
        if(world.isAirBlock(blockPos.add(0,1,0))) {
            world.setBlockState(blockPos.add(0,1,0), Blocks.GRASS.getDefaultState());
            world.setBlockState(blockPos.add(0,1,0), Blocks.AIR.getDefaultState());
        } else {
            IBlockState oldBlock = world.getBlockState(blockPos.add(0,1,0));
            world.setBlockState(blockPos.add(0,1,0), Blocks.AIR.getDefaultState());
            world.setBlockState(blockPos.add(0,1,0), oldBlock);
        }
    }

    private boolean haveItem(String block, int slot) {
        if(Block.getBlockFromName(block).getDefaultState().getMaterial().isLiquid()) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(Block.getBlockFromName(blockStructure.blocks.get(countBlocks).state.getBlock().getRegistryName().toString()));
            ItemStack bucket = FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
            if(inventory.getStackInSlot(slot).toString().equals(bucket.toString())) {
                this.isLiquid = true;
                return true;
            }
            return false;
        }

        for (Filters.changeItemFilter filter : filter) {
            if (filter.original.equals(block)) {
                if (inventory.getStackInSlot(slot).getItem().getRegistryName().toString().equals(filter.replace)) {
                    return true;
                }
            }
        }

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

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.checked = compound.getBoolean("checked");
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setBoolean("checked", this.checked);
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