package com.kyproject.justcopyit.templates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StructureTemplate {

    private ArrayList<BlockPlace.BlockState> blocks = new ArrayList<>();
    private BlockPlace structure;

    public void add(int x, int y, int z, IBlockState state) {
        blocks.add(new BlockPlace.BlockState(x,y,z,state));
    }

    public BlockPlace getStructure() {
        return structure;
    }

    public void create(String type, String name, EnumFacing facing) {
        structure = new BlockPlace(type, name, facing, this.blocks);
    }

    public String createNBTFile(String name, NBTTagCompound structure) {
        File file = new File("resources\\JustCopyIt\\structures\\" + name + ".dat");

        if(!file.exists()) {
            try {
                CompressedStreamTools.safeWrite(structure, file);
                return "Finished";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "File already exist";
        }

        return "Something went wrong!";
    }

    public NBTTagCompound getNBT(String fileName) {
        return this.getNBTFile(fileName);
    }

    private NBTTagCompound getNBTFile(String fileName) {
        try {
            return CompressedStreamTools.read(new File("resources\\JustCopyIt\\structures\\" + fileName + ".dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class BlockPlace {
        public String type;
        public String name;
        public EnumFacing facing;
        public ArrayList<BlockState> blocks;

        private BlockPlace(String type, String name, EnumFacing facing, ArrayList<BlockState> blocks) {
            this.type = type;
            this.name = name;
            this.facing = facing;
            this.blocks = blocks;
        }

        public static class BlockState {
            public int x;
            public int y;
            public int z;
            public IBlockState state;

            private BlockState(int x, int y, int z, IBlockState state) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.state = state;
            }
        }
    }
}
