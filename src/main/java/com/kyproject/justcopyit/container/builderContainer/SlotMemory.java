package com.kyproject.justcopyit.container.builderContainer;

import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotMemory extends SlotItemHandler {

    private int stackLimit = -1;

    public SlotMemory(IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        if(stack.getItem() == ModItems.MEMORY_CARD) {
            return true;
        } else {
            return false;
        }
    }
}
