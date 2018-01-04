package com.kyproject.justcopyit.block;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.client.GuiHandler;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockStructureBuilder extends BlockBase implements ITileEntityProvider {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockStructureBuilder(String name, Material material) {
        super(name, material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityBuilder te = (TileEntityBuilder) worldIn.getTileEntity(pos);

        if(playerIn.inventory.getCurrentItem().getItem().equals(ModItems.MEMORY_CARD)) {
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
                if (inventory != null) {
                    if (inventory.getStackInSlot(130).isEmpty()) {
                        inventory.insertItem(130, playerIn.inventory.getCurrentItem().copy().splitStack(1), false);
                        playerIn.inventory.getCurrentItem().splitStack(1);
                        return true;
                    }
                }
            }
        }

        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
            playerIn.openGui(JustCopyIt.instance, GuiHandler.GUI_BUILDER_CONTAINER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }


    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return super.canConnectRedstone(state, world, pos, side);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta);
        if(facing.getAxis()==EnumFacing.Axis.Y) {
            facing=EnumFacing.NORTH;
        }
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityBuilder te = (TileEntityBuilder) worldIn.getTileEntity(pos);
        if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
            IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
            if(inventory != null) {
                for (int i = 0;i < inventory.getSlots(); i++) {
                    if (inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                        EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));
                        float multiplier = 0.3f;
                        float motionX = worldIn.rand.nextFloat() - 0.5f;
                        float motionY = worldIn.rand.nextFloat() - 0.5f;
                        float motionZ = worldIn.rand.nextFloat() - 0.5f;

                        item.motionX = motionX * multiplier;
                        item.motionY = motionY * multiplier;
                        item.motionZ = motionZ * multiplier;

                        worldIn.spawnEntity(item);
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBuilder();
    }
}
