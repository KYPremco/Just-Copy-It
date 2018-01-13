package com.kyproject.justcopyit.tab;

import com.kyproject.justcopyit.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabJustCopyIt extends CreativeTabs {

    public CreativeTabJustCopyIt(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.BLUEPRINT);
    }
}
