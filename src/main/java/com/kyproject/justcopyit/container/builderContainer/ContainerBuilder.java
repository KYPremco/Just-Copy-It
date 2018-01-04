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
            addSlotToContainer(new SlotMemory(inventory, 130,212, 148));

            // SpeedUpgrade slot
            addSlotToContainer(new SlotSpeedUpgrade(inventory, 129, 212, 171));

            // Memory upgrade
            addSlotToContainer(new SlotMemoryUpgrade(inventory, 131, 212,191));


            // Container inventory
            for(int y = 0;y < 7;y++) {
                for(int x = 0; x < 13; x++) {
                    addSlotToContainer(new SlotBuilderInventory(inventory, x + (y * 13), 12 + x * 18, 17 + y * 18));
                }
            }


            // Main player inventory
            for(int y = 0;y < 3;y++) {
                for(int x = 0; x < 9; x++) {
                    addSlotToContainer(new Slot(inventoryPlayer, x + (y * 9) + 9, 48 + x * 18, 171 + y * 18));
                }
            }

            // Player hotbar
            for(int i = 0;i < 9;i++) {
                addSlotToContainer(new Slot(inventoryPlayer, i, 48 + (i * 18), 229));
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

            int containerSlots = 94;


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
