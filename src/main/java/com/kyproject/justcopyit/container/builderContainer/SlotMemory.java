package com.kyproject.justcopyit.container.builderContainer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static com.kyproject.justcopyit.init.ModItems.*;

public class SlotMemory extends SlotItemHandler {

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
        return stack.getItem() == BLUEPRINT || stack.getItem() == BLUEPRINT_CREATIVE;
    }
}
