package com.kyproject.justcopyit.item;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.ModBlocks;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBinder extends ItemBase {

    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);

    public ItemBinder(String name) {
        super(name);
        setCreativeTab(JustCopyIt.tabMyNewMod);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("type"))
        {
            tooltip.add("Type: " + stack.getTagCompound().getString("type"));
            tooltip.add("Pos: " + NBTUtil.getPosFromTag((NBTTagCompound) stack.getTagCompound().getTag("pos")));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();
        if(block.equals(ModBlocks.WORLD_MARKER) || block.equals(ModBlocks.WORLD_MARKER_MASTER)) {
            NBTTagCompound nbt;

            // Check if stack has NBT else create new
            if(player.getHeldItemMainhand().hasTagCompound()) {
                nbt = player.getHeldItemMainhand().getTagCompound();
            } else {
                nbt = new NBTTagCompound();
            }

            // If binder has a type
            if(nbt.hasKey("type")) {
                // Linking master to slave
                if(nbt.getString("type").equals("master") && block.equals(ModBlocks.WORLD_MARKER)) {
                    if(world.getBlockState(pos).getProperties().get(DAMAGE).equals(2)) {
                        if(!world.isRemote) {
                            player.sendMessage(new TextComponentString("Marker already linked"));
                        }
                    } else {
                        TileEntityWorldMarker te = (TileEntityWorldMarker) world.getTileEntity(NBTUtil.getPosFromTag((NBTTagCompound) player.getHeldItemMainhand().getTagCompound().getTag("pos")));
                        te.setLine(NBTUtil.getPosFromTag((NBTTagCompound) player.getHeldItemMainhand().getTagCompound().getTag("pos")), pos, player);

                    }
                    // after finish remove NBT
                    nbt.removeTag("type");
                    nbt.removeTag("pos");

                    // Linking slave to master
                } else if (nbt.getString("type").equals("slave") && block.equals(ModBlocks.WORLD_MARKER_MASTER)) {
                    TileEntityWorldMarker te = (TileEntityWorldMarker) world.getTileEntity(pos);
                    te.setLine(pos, NBTUtil.getPosFromTag((NBTTagCompound) player.getHeldItemMainhand().getTagCompound().getTag("pos")), player);

                    // after finish remove NBT
                    nbt.removeTag("type");
                    nbt.removeTag("pos");
                    // Cannot link master to master || slave to slave, remove NBT
                } else {
                    // Turn slave to red
                    if (nbt.getString("type").equals("slave")) {
                        world.setBlockState(NBTUtil.getPosFromTag((NBTTagCompound) player.getHeldItemMainhand().getTagCompound().getTag("pos")), ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 0));
                    }
                    if(!world.isRemote) {
                        player.sendMessage(new TextComponentString("Cannot link"));
                    }
                    nbt.removeTag("type");
                    nbt.removeTag("pos");
                }
                // Binder doesn't have a type
            } else {
                if(block.equals(ModBlocks.WORLD_MARKER_MASTER)) {
                    nbt.setString("type", "master");
                } else {
                    if(world.getBlockState(pos).getProperties().get(DAMAGE).equals(2)) {
                        if(!world.isRemote) {
                            player.sendMessage(new TextComponentString("Marker already linked"));
                        }
                    } else {
                        world.setBlockState(pos, ModBlocks.WORLD_MARKER.getDefaultState().withProperty(DAMAGE, 1));
                        nbt.setString("type", "slave");
                    }
                }
                nbt.setTag("pos", NBTUtil.createPosTag(pos));
                player.getHeldItemMainhand().setTagCompound(nbt);
            }
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

}
