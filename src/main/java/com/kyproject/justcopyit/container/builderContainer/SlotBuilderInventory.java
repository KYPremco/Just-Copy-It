package com.kyproject.justcopyit.container.builderContainer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotBuilderInventory extends SlotItemHandler {

    public SlotBuilderInventory(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return true;
//        if(stack.getItem() == Items.IRON_AXE || stack.getItem() == Items.IRON_PICKAXE || stack.getItem() == Items.IRON_HOE || stack.getItem() == ModItems.rubby || stack.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE)) {
//            return true;
//        } else {
//            return false;
//        }
    }
}
