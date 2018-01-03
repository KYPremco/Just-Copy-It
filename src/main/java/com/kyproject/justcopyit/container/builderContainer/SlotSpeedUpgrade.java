package com.kyproject.justcopyit.container.builderContainer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static com.kyproject.justcopyit.init.ModItems.UPGRADE_SPEED;

public class SlotSpeedUpgrade extends SlotItemHandler {

    public SlotSpeedUpgrade(IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 4;
    }

    @Override
    public int getSlotStackLimit() {
        return 4;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return stack.getItem() == UPGRADE_SPEED;
    }

}
