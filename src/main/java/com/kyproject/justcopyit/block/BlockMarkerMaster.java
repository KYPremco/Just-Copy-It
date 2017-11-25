package com.kyproject.justcopyit.block;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.ModBlocks;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMarkerMaster extends BlockBase implements ITileEntityProvider {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 5, 0.0625 * 5, 0.0625 * 5, 0.0625 * 11, 0.0625 * 11, 0.0625 * 11);
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0625 * 6, 0.0625 * 6, 0.0625 * 6, 0.0625 * 10, 0.0625 * 10, 0.0625 * 10);

    public BlockMarkerMaster(String name, Material material) {
        super(name, material);
        setCreativeTab(JustCopyIt.tabMyNewMod);
        setLightLevel(1F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }



    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityWorldMarker te = (TileEntityWorldMarker) worldIn.getTileEntity(pos);
        if(te != null) {
            if (te.slaveX != null) {
                worldIn.setBlockState(te.slaveX, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(TileEntityWorldMarker.DAMAGE, 0));
            }
            if (te.slaveY != null) {
                worldIn.setBlockState(te.slaveY, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(TileEntityWorldMarker.DAMAGE, 0));
            }
            if (te.slaveZ != null) {
                worldIn.setBlockState(te.slaveZ, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(TileEntityWorldMarker.DAMAGE, 0));
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityWorldMarker();
    }
}
