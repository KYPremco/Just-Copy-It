package com.kyproject.justcopyit.item;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemBase extends Item {

    ItemBase(String name)
    {
        this.setRegistryName(new ResourceLocation(JustCopyIt.MODID, name));
        this.setUnlocalizedName(name);
        setCreativeTab(JustCopyIt.creativeTabJustCopyIt);
    }

}
