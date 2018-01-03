package com.kyproject.justcopyit.util;


import com.google.common.base.Optional;
import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class NBTUtilFix  {

    public static IBlockState readBlockState(NBTTagCompound tag)
    {
        if (!tag.hasKey("Name", 8))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("Name")));
            IBlockState iblockstate = block.getDefaultState();

            if (tag.hasKey("Properties", 10))
            {
                NBTTagCompound nbttagcompound = tag.getCompoundTag("Properties");
                BlockStateContainer blockstatecontainer = block.getBlockState();

                for (String s : nbttagcompound.getKeySet())
                {
                    IProperty<?> iproperty = blockstatecontainer.getProperty(s);

                    if (iproperty != null)
                    {
                        iblockstate = setValueHelper(iblockstate, iproperty, s, nbttagcompound, tag);
                    }
                }
            }

            return iblockstate;
        }
    }

    private static <T extends Comparable<T>> IBlockState setValueHelper(IBlockState p_193590_0_, IProperty<T> p_193590_1_, String p_193590_2_, NBTTagCompound p_193590_3_, NBTTagCompound p_193590_4_)
    {
        Optional<T> optional = p_193590_1_.parseValue(p_193590_3_.getString(p_193590_2_).intern());

        if (optional.isPresent())
        {
            return p_193590_0_.withProperty(p_193590_1_, optional.get());
        }
        else
        {
            JustCopyIt.logger.warn("Unable to read property: {} with value: {} for blockstate: {}", p_193590_2_, p_193590_3_.getString(p_193590_2_), p_193590_4_.toString());
            return p_193590_0_;
        }
    }
}
