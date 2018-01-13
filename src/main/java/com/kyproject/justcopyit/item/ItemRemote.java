package com.kyproject.justcopyit.item;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.block.BlockStructureScanner;
import com.kyproject.justcopyit.client.GuiHandler;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemRemote extends ItemBase {
    public ItemRemote(String name) {
        super(name);
        setMaxStackSize(64);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
        if(playerIn.isSneaking()) {
            if(playerIn.getHeldItemMainhand().hasTagCompound()) {
                NBTTagCompound nbt = playerIn.getHeldItemMainhand().getTagCompound();
                nbt.removeTag("pos");
            }
        } else {
            if(playerIn.getHeldItemMainhand().hasTagCompound()) {
                NBTTagCompound nbt = playerIn.getHeldItemMainhand().getTagCompound();
                if(nbt.hasKey("pos")) {
                    BlockPos pos = NBTUtil.getPosFromTag(nbt.getCompoundTag("pos"));
                    if(world.getTileEntity(pos) != null && world.getBlockState(pos).getBlock() == ModBlocks.STRUCTURE_BUILDER) {
                        playerIn.openGui(JustCopyIt.instance, GuiHandler.GUI_REMOTE, world, pos.getX(), pos.getY(), pos.getZ());
                    } else {
                        if(world.isRemote) {
                            playerIn.sendMessage(new TextComponentString("§e[JCI] Cannot connect to builder"));
                        }
                    }
                } else {
                    if(world.isRemote) {
                        playerIn.sendMessage(new TextComponentString("§e[JCI] Remote isn't linked to a builder"));
                        playerIn.sendMessage(new TextComponentString("§e[JCI] Shift + Right click on a builder to link the remote"));
                    }
                }
            } else {
                if(world.isRemote) {
                    playerIn.sendMessage(new TextComponentString("§e[JCI] Remote isn't linked to a builder"));
                    playerIn.sendMessage(new TextComponentString("§e[JCI] Shift + Right click on a builder to link the remote"));
                }
            }
        }
        return super.onItemRightClick(world, playerIn, handIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.getBlockState(pos).getBlock().equals(ModBlocks.STRUCTURE_BUILDER)) {
            NBTTagCompound nbt;

            // Check if stack has NBT else create new
            if(player.getHeldItemMainhand().hasTagCompound()) {
                nbt = player.getHeldItemMainhand().getTagCompound();
            } else {
                nbt = new NBTTagCompound();
            }

            nbt.setTag("pos", NBTUtil.createPosTag(pos));
            player.getHeldItemMainhand().setTagCompound(nbt);
        } else {
            if(world.isRemote) {
                player.sendMessage(new TextComponentString("§e[JCI] Can only link to builder"));
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
