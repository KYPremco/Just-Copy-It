package com.kyproject.justcopyit.item;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMemoryCard extends ItemBase {

    public ItemMemoryCard(String name) {
        super(name);
        setMaxStackSize(64);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("structureList"))
        {
            tooltip.add("Blocks: " + stack.getTagCompound().getTagList("structureList", Constants.NBT.TAG_COMPOUND).tagCount());
        } else {
            tooltip.add("No structure");
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.getBlockState(pos).getBlock() == ModBlocks.STRUCTURE_BUILDER) {
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
