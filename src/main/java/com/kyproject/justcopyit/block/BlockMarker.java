package com.kyproject.justcopyit.block;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockMarker extends BlockBase {

    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 5, 0.0625 * 5, 0.0625 * 5, 0.0625 * 11, 0.0625 * 11, 0.0625 * 11);
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0625 * 6, 0.0625 * 6, 0.0625 * 6, 0.0625 * 10, 0.0625 * 10, 0.0625 * 10);

    public BlockMarker(String name, Material material) {
        super(name, material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DAMAGE, Integer.valueOf(0)));
        setCreativeTab(JustCopyIt.tabMyNewMod);
        setLightLevel(1F);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DAMAGE, (meta & 15) >> 2);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | (Integer) state.getValue(DAMAGE) << 2;
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{DAMAGE});
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
}
