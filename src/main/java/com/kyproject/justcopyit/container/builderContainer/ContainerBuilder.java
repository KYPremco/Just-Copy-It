package com.kyproject.justcopyit.container.builderContainer;

import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ContainerBuilder extends Container {

    public ContainerBuilder(InventoryPlayer inventoryPlayer, TileEntityBuilder tileEntityBuilder) {

        if(tileEntityBuilder.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
            IItemHandler inventory = tileEntityBuilder.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            // Memory slot
            addSlotToContainer(new SlotMemory(inventory, 54,192,54));

            // Container inventory
            for(int y = 0;y < 6;y++) {
                for(int x = 0; x < 9; x++) {
                    addSlotToContainer(new SlotBuilderInventory(inventory, x + (y * 9), 8 + x * 18, 18 + y * 18));
                }
            }



            // Main player inventory
            for(int y = 0;y < 3;y++) {
                for(int x = 0; x < 9; x++) {
                    addSlotToContainer(new Slot(inventoryPlayer, x + (y * 9) + 9, 8 + x * 18, 140 + y * 18));
                }
            }

            // Player hotbar
            for(int i = 0;i < 9;i++) {
                addSlotToContainer(new Slot(inventoryPlayer, i, 8 + (i * 18), 198));
            }
        }

    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if((slot != null) && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            int containerSlots = 55;

            if(index < containerSlots) {
                if(!this.mergeItemStack(stackInSlot, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if(stackInSlot.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChange(ItemStack.EMPTY, ItemStack.EMPTY);
            }

            slot.onTake(player, stackInSlot);
        }
        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
