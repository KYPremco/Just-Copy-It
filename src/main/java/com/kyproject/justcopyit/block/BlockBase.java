package com.kyproject.justcopyit.block;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.block.Block;

import net.minecraft.block.material.Material;

public class BlockBase extends Block {

    BlockBase(String name, Material material) {
        super(material);

        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        setCreativeTab(JustCopyIt.creativeTabJustCopyIt);
    }

}