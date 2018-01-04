package com.kyproject.justcopyit.item;

import com.kyproject.justcopyit.init.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRemote extends ItemBase {
    public ItemRemote(String name) {
        super(name);
        setMaxStackSize(64);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
        if(playerIn.isSneaking()) {
            if(playerIn.getHeldItemMainhand().hasTagCompound()) {
                NBTTagCompound nbt = playerIn.getHeldItemMainhand().getTagCompound();
                nbt.removeTag("worlMarker");
            }
        }
        return super.onItemRightClick(world, playerIn, handIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.getBlockState(pos).getBlock().equals(ModBlocks.WORLD_MARKER_MASTER)) {
            NBTTagCompound nbt;

            // Check if stack has NBT else create new
            if(player.getHeldItemMainhand().hasTagCompound()) {
                nbt = player.getHeldItemMainhand().getTagCompound();
            } else {
                nbt = new NBTTagCompound();
            }

            if(!nbt.hasKey("worlMarker")) {
                System.out.println("test");
                nbt.setBoolean("worlMarker", true);
                NBTUtil.createPosTag(pos);
            }

            player.getHeldItemMainhand().setTagCompound(nbt);
        }
        return EnumActionResult.SUCCESS;
    }
}
