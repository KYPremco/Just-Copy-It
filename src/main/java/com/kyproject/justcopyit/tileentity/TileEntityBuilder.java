package com.kyproject.justcopyit.tileentity;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.Filters;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.util.NBTUtilFix;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    private ItemStackHandler inventory = new ItemStackHandler(132);
    private Capability<IEnergyStorage> energyCapability = CapabilityEnergy.ENERGY;
    public EnergyStorage energy = new EnergyStorage(100000);;

    private boolean blockIsBuilding = false;
    private int countBlocks = 0;
    private int counter = 0;
    private boolean isLiquid = false;
    private boolean isSeed = false;
    private boolean checked = false;
    private boolean removedDurability = false;
    public int rangeX = 0;
    public int rangeY = 0;
    public int rangeZ = 0;
    public ItemStack needItem = null;
    public String texture = "blue";

    public int movableX = 2;
    public int movableY = 2;
    public int movableZ = 2;


    public void buttonPressed(int id) {
        switch (id) {
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

    public int getEnergy() {
        return (energy.getEnergyStored() * 94 / energy.getMaxEnergyStored());
    }

    private IBlockState getState() {
        return world.getBlockState(pos);
    }

    public void startStructure() {
        this.blockStructure = this.getStructure();
        this.checkStructureBuild();
        this.removedDurability = false;
        this.countBlocks = 0;
        this.counter = 0;
        this.texture = "orange";
        this.blockIsBuilding = true;
    }

    @SideOnly(Side.CLIENT)
    private void displayCurrentItem() {
        if(blockStructure.blocks.get(countBlocks).state != null) {
            this.needItem = blockStructure.blocks.get(countBlocks).state.getBlock().getItem(world, null, blockStructure.blocks.get(countBlocks).state);
        }
    }

    private int energyToPlace() {
        float basePower = 16;
        double speedPower = basePower + 20 / (5 - this.inventory.getStackInSlot(129).getCount()); // 25, 26, 27, 31, 41 / correct
        int memory = this.inventory.getStackInSlot(131).getCount() + 1;
        double memoryPower = memory * 1.5; // 21
        double total = (speedPower * memoryPower);

        return (int) total;
    }

    private StructureTemplate.BlockPlace getStructure() {
        StructureTemplate structureTemplate = new StructureTemplate();

        // Reading
        if(inventory.getStackInSlot(130) != ItemStack.EMPTY) {
            if(inventory.getStackInSlot(130).getItem() == ModItems.MEMORY_CARD || inventory.getStackInSlot(130).getItem() == ModItems.MEMORY_CARD_CREATIVE) {
                if(inventory.getStackInSlot(130).hasTagCompound()) {
                    if(inventory.getStackInSlot(130).getTagCompound().hasKey("type")) {
                        EnumFacing forward = EnumFacing.getFront(this.getBlockMetadata());
                        NBTTagCompound nbt = inventory.getStackInSlot(130).getTagCompound();
                        NBTTagList tagList = nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                        for(int i=0;i < tagList.tagCount();i++) {
                            NBTTagCompound tag = tagList.getCompoundTagAt(i);
                            int x = tag.getInteger("blockX" + i);
                            int z = tag.getInteger("blockZ" + i);
                            int y = tag.getInteger("blockY" + i);
                            IBlockState state = NBTUtilFix.readBlockState(tag);
                            NBTTagCompound nbtBlock = null;
                            if(tag.hasKey("nbt")) {
                                nbtBlock = tag.getCompoundTag("nbt");
                            }

                            int[] newBlockPosXYZ;
                            newBlockPosXYZ = this.getRotateStructure(EnumFacing.byName(nbt.getString("facing")), forward, x, y, z);

                            structureTemplate.add(newBlockPosXYZ[0], newBlockPosXYZ[1], newBlockPosXYZ[2], state, nbtBlock);
                        }

                        structureTemplate.create(nbt.getString("type"), nbt.getString("name"), EnumFacing.byName(nbt.getString("facing")), nbt.getInteger("durability"), nbt.getInteger("rangeX"), nbt.getInteger("rangeY"), nbt.getInteger("rangeZ"));

                        return structureTemplate.getStructure();
                    }
                }
            }
        }

        structureTemplate.create("card", "card", EnumFacing.NORTH, -1, 0,0,0);
        return structureTemplate.getStructure();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void update() {
        if(!world.isRemote) {
            this.sendUpdates();
        }

        if(inventory.getStackInSlot(130) != ItemStack.EMPTY && inventory.getStackInSlot(130).hasTagCompound() && inventory.getStackInSlot(130).getTagCompound().hasKey("rangeX")) {
            NBTTagCompound nbt = inventory.getStackInSlot(130).getTagCompound();
            int[] range =  this.getRotateStructure(EnumFacing.byName(nbt.getString("facing")), EnumFacing.getFront(this.getBlockMetadata()), nbt.getInteger("rangeX"), nbt.getInteger("rangeY"), nbt.getInteger("rangeZ"));

            this.rangeX = range[0];
            this.rangeY = range[1];
            this.rangeZ = range[2];

        } else {
            this.rangeX = 0;
            this.rangeY = 0;
            this.rangeZ = 0;
        }

        if (this.blockIsBuilding) {
            this.energy.extractEnergy(2,false);
            int tickCounter = this.getBuildSpeed();
            if (counter == tickCounter) {
                if (blockStructure.blocks.size() == countBlocks) {
                    if (this.removedDurability) {
                        this.memorycardDurability();
                    }
                    countBlocks = blockStructure.blocks.size();
                    this.blockIsBuilding = false;
                    this.needItem = null;
                    this.texture = "green";
                } else {
                    if (inventory.getStackInSlot(130).getItem().equals(ModItems.MEMORY_CARD_CREATIVE)) {
                        EnumFacing facing_new = EnumFacing.getFront(this.getBlockMetadata());
                        EnumFacing facing_original = blockStructure.facing;

                        for (int countBlocks = 0; countBlocks < blockStructure.blocks.size(); countBlocks++) {
                            BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                            IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                            stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);
                            world.setBlockState(blockPos, stateBlock, 3);


                            TileEntity tileEntity = world.getTileEntity(blockPos);
                            if (tileEntity != null) {
                                blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("x", blockPos.getX());
                                blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("y", blockPos.getY());
                                blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("z", blockPos.getZ());
                                tileEntity.readFromNBT(blockStructure.blocks.get(countBlocks).nbtTagCompound);
                            }

                        }
                        this.memorycardDurability();
                        countBlocks = blockStructure.blocks.size();
                    } else if(this.energy.getEnergyStored() != 0) {
                        boolean build;

                        if (!inventory.getStackInSlot(130).isEmpty()) {
                            if (world.isAirBlock(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z))) {
                                build = true;
                            } else {
                                if (world.getBlockState(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z)).getMaterial().isLiquid()) {
                                    build = true;
                                } else {
                                    build = world.getBlockState(new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z)).getBlock().isReplaceable(world, new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z));
                                }
                            }

                            if (!this.removedDurability) {
                                this.removedDurability = true;
                            }

                            if (build) {
                                boolean skipBlock = true;
                                this.texture = "red";
                                for(int memoryUpgrade = 0;memoryUpgrade <= this.inventory.getStackInSlot(131).getCount();memoryUpgrade++) {
                                    if (blockStructure.blocks.size() != countBlocks) {
                                        for (int slot = 0; slot < inventory.getSlots() - 2; slot++) {
                                            if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                                                this.displayCurrentItem();
                                                if (!inventory.getStackInSlot(slot).isEmpty()) {
                                                    if (this.haveItem(blockStructure.blocks.get(countBlocks).state, slot)) {
                                                        if (this.energy.getEnergyStored() >= this.energyToPlace()) {
                                                            BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x + this.movableX, blockStructure.blocks.get(countBlocks).y + this.movableY, blockStructure.blocks.get(countBlocks).z + this.movableZ);
                                                            IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                                                            EnumFacing facing_new = EnumFacing.getFront(this.getBlockMetadata());
                                                            EnumFacing facing_original = blockStructure.facing;
                                                            stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);
                                                            if (this.isSeed) {
                                                                world.setBlockState(blockPos, stateBlock.getBlock().getDefaultState(), 3);
                                                            } else {
                                                                world.setBlockState(blockPos, stateBlock, 3);
                                                                TileEntity tileEntity = world.getTileEntity(blockPos);
                                                                if (tileEntity != null) {
                                                                    blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("x", blockPos.getX());
                                                                    blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("y", blockPos.getY());
                                                                    blockStructure.blocks.get(countBlocks).nbtTagCompound.setInteger("z", blockPos.getZ());
                                                                    tileEntity.readFromNBT(blockStructure.blocks.get(countBlocks).nbtTagCompound);
                                                                }
                                                            }

                                                            if (!this.isLiquid) {
                                                                inventory.extractItem(slot, 1, false);
                                                            } else {
                                                                inventory.setStackInSlot(slot, new ItemStack(Items.BUCKET));
                                                                this.updateWaterTick(blockPos);
                                                            }

                                                            this.energy.extractEnergy(this.energyToPlace(), false);
                                                            this.texture = "orange";
                                                            this.isLiquid = false;
                                                            skipBlock = false;
                                                            countBlocks++;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (skipBlock && this.checked) {
                                    countBlocks++;
                                }
                            } else {
                                countBlocks++;
                            }
                        } else {
                            this.blockIsBuilding = false;
                        }
                    } else {
                        this.needItem = null;
                        this.texture = "grey";
                    }
                }
                counter = 0;
            } else {
                counter++;
            }
        } else if(this.energy.getEnergyStored() == 0) {
            this.texture = "grey";
        } else {
            this.texture = "blue";
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

    private int getBuildSpeed() {
        if(this.counter > 4 - inventory.getStackInSlot(129).getCount()){
            this.counter = 0;
        }

        if(!inventory.getStackInSlot(129).isEmpty()) {
            return 4 - inventory.getStackInSlot(129).getCount();
        }
        return 4;
    }

    private void memorycardDurability() {
        if(blockStructure.durability == 1) {
            inventory.extractItem(130, 1, false);
        } else {
            NBTTagCompound nbt = inventory.getStackInSlot(130).getTagCompound();
            if(nbt != null) {
                if(nbt.hasKey("durability")) {
                    if(nbt.getInteger("durability") > 1) {
                        int durability = nbt.getInteger("durability");
                        nbt.setInteger("durability", --durability);
                        inventory.getStackInSlot(130).setTagCompound(nbt);
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

    private boolean haveItem(IBlockState state, int slot) {
        if(state.getBlock().getDefaultState().getMaterial().isLiquid()) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(Block.getBlockFromName(blockStructure.blocks.get(countBlocks).state.getBlock().getRegistryName().toString()));
            ItemStack bucket = FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));

            if(bucket.getTagCompound() != null) {
                if(bucket.getTagCompound().hasKey("FluidName")) {
                    FluidStack inventoryBucket = FluidStack.loadFluidStackFromNBT(inventory.getStackInSlot(slot).getTagCompound());
                    if(inventoryBucket != null) {
                        String blockFluid = bucket.getTagCompound().getString("FluidName");
                        String inventoryBucketFluid = inventoryBucket.getFluid().getName();
                        if(inventoryBucketFluid.equals(blockFluid)) {
                            this.isLiquid = true;
                            return true;
                        }
                    }
                }
            } else {
                if(inventory.getStackInSlot(slot).toString().equals(bucket.toString())) {
                    this.isLiquid = true;
                    return true;
                }
            }

            return false;
        }

        for (Filters.changeItemFilter filter : filter) {
            if (Objects.equals(filter.original.intern(), state.getBlock().getRegistryName().toString())) {
                if (inventory.getStackInSlot(slot).getItem().getRegistryName().toString().equals(filter.replace)) {
                    if(inventory.getStackInSlot(slot).getItem() instanceof ItemSeeds) {
                        this.isSeed = true;
                    }
                    return true;
                }
            }
        }

        if(inventory.getStackInSlot(slot).getItem() instanceof ItemSeeds) {
            this.isSeed = true;
        }

        return inventory.getStackInSlot(slot).isItemEqual(state.getBlock().getItem(world, null, state));
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setBoolean("checked", this.checked);
        compound.setTag("energy", this.energyCapability.writeNBT(this.energy, null));

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.checked = compound.getBoolean("checked");
        if(compound.hasKey("energy")) {
            this.energyCapability.readNBT(this.energy, null, compound.getTag("energy"));
        }
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
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if(capability == CapabilityEnergy.ENERGY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.inventory;
        if(capability == CapabilityEnergy.ENERGY && facing != EnumFacing.DOWN)
            return (T) this.energy;
        return super.getCapability(capability, facing);
    }



}