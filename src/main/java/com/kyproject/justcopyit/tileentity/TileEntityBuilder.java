package com.kyproject.justcopyit.tileentity;
import com.kyproject.justcopyit.init.Filters;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.templates.StructureTemplateManager;
import com.kyproject.justcopyit.util.NBTUtilFix;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class TileEntityBuilder extends TileEntity implements ITickable {

    private StructureTemplate.BlockPlace blockStructure;
    public static ArrayList<Filters.changeItemFilter> filter = new ArrayList<>();

    public FluidTank fluidTank = new FluidTank(20000);

    private ItemStackHandler inventory = new ItemStackHandler(132);
    private Capability<IEnergyStorage> energyCapability = CapabilityEnergy.ENERGY;
    public EnergyStorage energy = new EnergyStorage(100000);

    private boolean blockIsBuilding = false;
    private boolean blockIsDemolishing = false;
    private int countBlocks = 0;
    private int counter = 0;
    private boolean isLiquid = false;
    private boolean checked = false;
    private boolean removedDurability = false;
    public EnumFacing facing;
    public int rangeX = 0;
    public int rangeY = 0;
    public int rangeZ = 0;
    public ItemStack needItem = null;
    public String texture = "blue";

    public int movableX = 0;
    public int movableY = 0;
    public int movableZ = 0;

    public void buttonPressed(int id) {
        switch (id) {
            case 1:
                this.startStructure();
                break;
            case 2:
                this.setChecked();
                break;
            case 3:
                this.startDemolish();
                break;
            case 4:
                movableX++;
                break;
            case 5:
                movableX--;
                break;
            case 6:
                movableY++;
                break;
            case 7:
                movableY--;
                break;
            case 8:
                movableZ++;
                break;
            case 9:
                movableZ--;
                break;
            case 10:
                movableX = 0;
                break;
            case 11:
                movableY = 0;
                break;
            case 12:
                movableZ = 0;
                break;
            case 13:
                if(facing == null) {
                    facing = EnumFacing.getFront(this.getBlockMetadata()).rotateY();
                } else {
                    facing = facing.rotateY();
                }
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

    public FluidStack getFluid() {
        return fluidTank.getFluid();
    }

    private IBlockState getState() {
        return world.getBlockState(pos);
    }

    public void startStructure() {
        System.out.println(EnumFacing.getFront(this.getBlockMetadata()));


//        this.blockIsDemolishing = false;
//        this.blockIsBuilding = false;
//        this.blockStructure = this.getStructure();
//        this.checkStructureBuild();
//        this.removedDurability = false;
//        this.countBlocks = 0;
//        this.counter = 0;
//        this.texture = "orange";
//        this.blockIsBuilding = true;
    }

    public void startDemolish() {
        this.blockIsBuilding = false;
        this.blockIsDemolishing = false;
        this.blockStructure = this.getStructure();
        this.checkStructureBuild();
        this.removedDurability = false;
        this.countBlocks = this.blockStructure.blocks.size() - 1;
        this.counter = 0;
        this.texture = "orange";
        this.blockIsDemolishing = true;
    }

    private void displayCurrentItem() {
        if(world.isRemote) {
            if(blockStructure.blocks.get(countBlocks).state != null) {
                StructureTemplateManager structureTemplateManager = new StructureTemplateManager(world);
                this.needItem = structureTemplateManager.getItem(blockStructure.blocks.get(countBlocks).state);
            }
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
            if(inventory.getStackInSlot(130).getItem() == ModItems.BLUEPRINT || inventory.getStackInSlot(130).getItem() == ModItems.BLUEPRINT_CREATIVE) {
                if(inventory.getStackInSlot(130).hasTagCompound()) {
                    if(inventory.getStackInSlot(130).getTagCompound().hasKey("type")) {
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
                            newBlockPosXYZ = this.getRotateStructure(EnumFacing.byName(nbt.getString("facing")), x, y, z);

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
        } else {
            if(inventory.getStackInSlot(130) != ItemStack.EMPTY && inventory.getStackInSlot(130).hasTagCompound() && inventory.getStackInSlot(130).getTagCompound().hasKey("rangeX")) {
                NBTTagCompound nbt = inventory.getStackInSlot(130).getTagCompound();
                int[] range =  this.getRotateStructureDisplay(EnumFacing.byName(nbt.getString("facing")), nbt.getInteger("rangeX"), nbt.getInteger("rangeY"), nbt.getInteger("rangeZ"));

                this.rangeX = range[0];
                this.rangeY = range[1];
                this.rangeZ = range[2];

            } else {
                this.needItem = null;
                this.rangeX = 0;
                this.rangeY = 0;
                this.rangeZ = 0;
            }
        }

        if(this.blockIsDemolishing) {
            this.demolishStructure();
        }

        if (this.blockIsBuilding) {
            this.buildStructure();
        } else if(this.energy.getEnergyStored() == 0) {
            this.texture = "grey";
        } else {
            this.texture = "blue";
        }
    }

    private void demolishStructure() {
        int creative;
        if(!inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
            this.energy.extractEnergy(2, false);
            creative = 0;
        } else {
            creative = 3 * this.inventory.getStackInSlot(131).getCount() + 10;
        }
        int tickCounter = this.getBuildSpeed();
        if (counter == tickCounter) {
            for(int memoryUpgrade = 0;memoryUpgrade <= this.inventory.getStackInSlot(131).getCount() + creative;memoryUpgrade++) {
                if (countBlocks != -1) {
                    if (blockStructure.blocks.get(countBlocks).isPlaced) {
                        EnumFacing facing_new = this.facing;
                        EnumFacing facing_original = blockStructure.facing;
                        BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                        IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                        stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);
                        if (countBlocks != -1) {
                            for (int slot = 0; slot < inventory.getSlots() - 3; slot++) {
                                if (inventory.getStackInSlot(slot).isItemEqual(stateBlock.getBlock().getItem(world, null, stateBlock)) && inventory.getStackInSlot(slot).getMaxStackSize() != inventory.getStackInSlot(slot).getCount()) {
                                    if (!world.isRemote) {
                                        inventory.insertItem(slot, stateBlock.getBlock().getItem(world, null, stateBlock), false);
                                        world.destroyBlock(blockPos, false);
                                    }
                                    break;
                                } else {
                                    if (inventory.getStackInSlot(slot).isEmpty()) {
                                        if (!world.isRemote) {
                                            inventory.insertItem(slot, stateBlock.getBlock().getItem(world, null, stateBlock), false);
                                            world.destroyBlock(blockPos, false);
                                        }
                                        break;
                                    }
                                }
                            }
                            countBlocks--;
                        } else {
                            this.blockIsDemolishing = false;
                        }
                    } else {
                        countBlocks--;
                    }
                } else {
                    this.blockIsDemolishing = false;
                }
            }
            counter = 0;
        } else {
            counter++;
        }
    }

    private void buildStructure() {
        int creative;
        if(!inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
            this.energy.extractEnergy(2, false);
            creative = 0;
        } else {
            creative = 10 * this.inventory.getStackInSlot(131).getCount() + 10;
        }

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

                EnumFacing facing_new = this.facing;
                EnumFacing facing_original = blockStructure.facing;

                if(this.energy.getEnergyStored() != 0 || inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
                    boolean canBuild = false;

                    if (!inventory.getStackInSlot(130).isEmpty()) {
                        for(int memoryUpgrade = 0;memoryUpgrade <= this.inventory.getStackInSlot(131).getCount() + creative;memoryUpgrade++) {
                            if (blockStructure.blocks.size() != countBlocks) {
                                if(world.isRemote) {
                                    this.displayCurrentItem();
                                }

                                while (!canBuild) {
                                    if (!blockStructure.blocks.get(countBlocks).isPlaced) {
                                        BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                                        if(world.mayPlace(blockStructure.blocks.get(countBlocks).state.getBlock(), blockPos, false, EnumFacing.NORTH, null)) {
                                            canBuild = true;
                                        } else {
                                            if(blockStructure.blocks.size() != countBlocks + 1) {
                                                countBlocks++;
                                            } else {
                                                needItem = null;
                                                blockIsBuilding = false;
                                                break;
                                            }
                                        }
                                    } else {
                                        if (blockStructure.blocks.size() != countBlocks + 1) {
                                            countBlocks++;
                                        } else {
                                            break;
                                        }
                                    }
                                }

                                if (!this.removedDurability) {
                                    this.removedDurability = true;
                                }

                                if (canBuild) {
                                    System.out.println("BUILD");
                                    boolean skipBlock = true;
                                    boolean build = false;
                                    int inventorySlot = 0;

                                    if (inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
                                        build = true;
                                    } else {
                                        for (int slot = 0; slot < inventory.getSlots() - 2; slot++) {
                                            if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                                                if (!inventory.getStackInSlot(slot).isEmpty()) {
                                                    if (this.haveItem(blockStructure.blocks.get(countBlocks).state, slot) && this.energy.getEnergyStored() >= this.energyToPlace()) {
                                                        build = true;
                                                        inventorySlot = slot;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (build) {
                                        StructureTemplateManager templateManager = new StructureTemplateManager(world);
                                        BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(countBlocks).x, blockStructure.blocks.get(countBlocks).y, blockStructure.blocks.get(countBlocks).z);
                                        IBlockState stateBlock = blockStructure.blocks.get(countBlocks).state;
                                        stateBlock = this.getStateWithRotation(facing_original, facing_new, stateBlock);

                                        templateManager.placeBlockInWorld(blockPos, stateBlock, blockStructure.blocks.get(countBlocks).nbtTagCompound);

                                        if (!inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
                                            templateManager.removeItemFromContainer(inventory, inventorySlot, isLiquid);
                                            this.energy.extractEnergy(this.energyToPlace(), false);
                                        }

                                        this.texture = "orange";
                                        this.isLiquid = false;
                                        skipBlock = false;
                                        countBlocks++;
                                    } else {
                                        this.texture = "red";
                                    }
                                    if (skipBlock && this.checked) {
                                        countBlocks++;
                                    }

                                    canBuild = false;
                                }
                            }
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
    }

    private void checkStructureBuild() {
        for(int block = 0; block < blockStructure.blocks.size();block++) {
            BlockPos blockPos = new BlockPos(pos).add(blockStructure.blocks.get(block).x, blockStructure.blocks.get(block).y, blockStructure.blocks.get(block).z);
            IBlockState structureState = blockStructure.blocks.get(block).state;
            IBlockState currentState = world.getBlockState(blockPos).getActualState(world, blockPos);
            ItemStack structureItem = structureState.getBlock().getItem(world, null, structureState);
            ItemStack currentItem = currentState.getBlock().getItem(world, null, currentState);

            if(structureState.getMaterial().isLiquid() && currentState.getMaterial().isLiquid()) {
                if(structureState == currentState) {
                    blockStructure.blocks.get(block).isPlaced = true;
                }
            } else {
                if(structureItem.isItemEqual(currentItem)) {
                    blockStructure.blocks.get(block).isPlaced = true;
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

    private int[] getRotateStructureDisplay(EnumFacing origin, int oldX, int oldY, int oldZ) {
        int x;
        int z;
        if(facing == null) {
            facing = EnumFacing.getFront(this.getBlockMetadata());
        }

        if(origin.getAxis() == facing.getAxis()) {
            if(origin.getName().equals(facing.getName())) {
                //0
                x = (oldX);
                z = (oldZ);
            } else {
                // 180
                x = (-oldX);
                z = (-oldZ);
            }
        } else {
            if(origin.rotateY().getName().equals(facing.getName())){
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

    private int[] getRotateStructure(EnumFacing origin, int oldX, int oldY, int oldZ) {
        int x;
        int z;
        if(facing == null) {
            facing = EnumFacing.getFront(this.getBlockMetadata());
        }

        if(origin.getAxis() == facing.getAxis()) {
            if(origin.getName().equals(facing.getName())) {
                //0
                x = (oldX) + this.movableX;
                z = (oldZ) + this.movableZ;
            } else {
                // 180
                x = (-oldX) + this.movableX;
                z = (-oldZ) + this.movableZ;
            }
        } else {
            if(origin.rotateY().getName().equals(facing.getName())){
                //90
                x = (-oldZ) + this.movableX;
                z = (oldX) + this.movableZ;
            } else {
                //270
                x = (oldZ) + this.movableX;
                z = (-oldX) + this.movableZ;
            }
        }

        int[] posXYZ = new int[3];
        posXYZ[0] = x;
        posXYZ[1] = oldY + this.movableY;
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
        if(inventory.getStackInSlot(130).getItem().equals(ModItems.BLUEPRINT_CREATIVE)) {
            return true;
        }

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
                    return true;
                }
            }
        }


        return inventory.getStackInSlot(slot).isItemEqual(state.getBlock().getItem(world, null, state));
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 10000;
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
        this.fluidTank.writeToNBT(compound);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.checked = compound.getBoolean("checked");
        this.fluidTank.readFromNBT(compound);
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
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.inventory;
        if(capability == CapabilityEnergy.ENERGY && facing != EnumFacing.DOWN)
            return (T) this.energy;
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN)
            return (T) this.fluidTank;
        return super.getCapability(capability, facing);
    }
}