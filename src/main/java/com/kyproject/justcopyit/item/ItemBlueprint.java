package com.kyproject.justcopyit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.sql.SQLType;
import java.util.List;
import java.util.Objects;

public class ItemBlueprint extends ItemBase {

    public ItemBlueprint(String name) {
        super(name);
        setMaxStackSize(64);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("type"))
        {
            if(Objects.equals(stack.getTagCompound().getString("type"), "card")) {
                tooltip.add("Blocks: " + stack.getTagCompound().getTagList("blocks", Constants.NBT.TAG_COMPOUND).tagCount());
            } else {
                if(stack.getTagCompound() != null) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    tooltip.add("Name: " + nbt.getString("name"));

                    if(nbt.hasKey("durability")) {
                        if(nbt.getInteger("durability") > 0) {
                            tooltip.add("Durability: " + nbt.getInteger("durability"));
                        }
                    }
                    tooltip.add("Blocks: " + nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND).tagCount());
                }
            }
        } else {
            tooltip.add("Empty");
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
