package com.kyproject.justcopyit.tileentity;

import com.kyproject.justcopyit.init.ModBlocks;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

public class TileEntityWorldMarker extends TileEntity implements ITickable{

    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);

    public int rangeX = 0;
    public int rangeY = 0;
    public int rangeZ = 0;

    public BlockPos slaveX;
    public BlockPos slaveY;
    public BlockPos slaveZ;


    public void setLine(BlockPos master, BlockPos slave, EntityPlayer player) {

        int masterX = master.getX(), masterY = master.getY(), masterZ = master.getZ();
        int slaveX = slave.getX(), slaveY = slave.getY(), slaveZ = slave.getZ();

        boolean x = masterX != slaveX;
        boolean y = masterY != slaveY;
        boolean z = masterZ != slaveZ;

        if(x && !z && !y) {
            if(this.slaveX == null) {
                this.rangeX = slaveX - masterX;
                this.slaveX = slave;
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 2));
                this.sendMessage("Linked", player);
            } else {
                this.sendMessage("Another X slave is already linked", player);
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 0));
            }
        } else if (y && !x && !z) {
            if(this.slaveY == null) {
                this.rangeY = slaveY - masterY;
                this.slaveY = slave;
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 2));
                this.sendMessage("Linked", player);
            } else {
                this.sendMessage("Another Y slave is already linked", player);
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 0));
            }
        } else if(z && !x && !y) {
            if(this.slaveZ == null) {
                this.rangeZ = slaveZ - masterZ;
                this.slaveZ = slave;
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 2));
                this.sendMessage("Linked", player);
            } else {
                this.sendMessage("Another Z slave is already linked", player);
                world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 0));
            }
        } else {
            world.setBlockState(slave, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 0));
            this.sendMessage("Slaves need to be in a straight line of the master", player);
        }

        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    private void sendMessage(String message, EntityPlayer player) {
        if(!world.isRemote) {
            player.sendMessage(new TextComponentString(message));
        }
    }

    @Override
    public void update() {
        if(slaveX != null) {
            if (!world.getBlockState(slaveX).getBlock().equals(ModBlocks.WORLD_MARKER)) {
                rangeX = 0;
                slaveX = null;
            }
        }
        if(slaveY != null) {
            if (!world.getBlockState(slaveY).getBlock().equals(ModBlocks.WORLD_MARKER)) {
                rangeY = 0;
                slaveY = null;
            }
        }
        if(slaveZ != null) {
            if (!world.getBlockState(slaveZ).getBlock().equals(ModBlocks.WORLD_MARKER)) {
                rangeZ = 0;
                slaveZ = null;
            }
        }
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 10000;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    //Some other stuff
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if(compound.hasKey("slaveX")) {
            slaveX = NBTUtil.getPosFromTag(compound.getCompoundTag("slaveX"));
            rangeX = compound.getInteger("rangeX");
        }
        if(compound.hasKey("slaveY")) {
            slaveY = NBTUtil.getPosFromTag((NBTTagCompound) compound.getTag("slaveY"));
            rangeY = compound.getInteger("rangeY");
        }
        if(compound.hasKey("slaveZ")) {
            slaveZ = NBTUtil.getPosFromTag((NBTTagCompound) compound.getTag("slaveZ"));
            rangeZ = compound.getInteger("rangeZ");
        }

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if(slaveX != null) {
            compound.setTag("slaveX", NBTUtil.createPosTag(slaveX));
            compound.setInteger("rangeX", rangeX);
        }
        if(slaveY != null) {
            compound.setTag("slaveY", NBTUtil.createPosTag(slaveY));
            compound.setInteger("rangeY", rangeY);
        }
        if(slaveZ != null) {
            compound.setTag("slaveZ", NBTUtil.createPosTag(slaveZ));
            compound.setInteger("rangeZ", rangeZ);
        }

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

}
