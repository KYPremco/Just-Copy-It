package com.kyproject.justcopyit.templates;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class StructureTemplate {

    public static ArrayList<String> blockItemFilter = new ArrayList<>();

    private ArrayList<BlockPlace.BlockState> blockLayer = new ArrayList<>();
    private ArrayList<BlockPlace.BlockState> blockLayerTop = new ArrayList<>();
    private ArrayList<BlockPlace.BlockState> liquidLayer = new ArrayList<>();

    private ArrayList<BlockPlace.BlockState> blocks = new ArrayList<>();
    private BlockPlace structure;

    public void addLayer(String layer, int x, int y, int z, IBlockState state) {
        switch (layer) {
            case "blockLayer":
                this.setLayer(x, y, z, state);
                break;
            case "liquid":
                liquidLayer.add(new BlockPlace.BlockState(x,y,z,state));
                break;
            default:
                break;
        }
    }

    private void setLayer(int x, int y, int z, IBlockState state) {
        boolean inFilter = false;
        for(String blockString : blockItemFilter) {
            if(blockString != null) {
                Block blockInFilter = Block.getBlockFromName(blockString);
                if(blockInFilter != null) {
                    if(blockInFilter.equals(state.getBlock())) {
                        blockLayerTop.add(new BlockPlace.BlockState(x,y,z,state));
                        inFilter = true;
                        break;
                    }
                }
            }
        }
        if(!inFilter) {
            blockLayer.add(new BlockPlace.BlockState(x,y,z,state));
        }
    }

    public void add(int x, int y, int z, IBlockState state) {
        blocks.add(new BlockPlace.BlockState(x,y,z,state));
    }

    public void combine() {
        blocks.addAll(blockLayer);
        blocks.addAll(blockLayerTop);
        blocks.addAll(liquidLayer);
    }

    public BlockPlace getStructure() {
        return structure;
    }

    public void create(String type, String name, EnumFacing facing) {
        structure = new BlockPlace(type, name, facing, this.blocks);
    }

    public void loadBlockItemFilter() {
        try {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            blockItemFilter = new Gson().fromJson(new FileReader("resources\\JustCopyIt\\itemsOnBlocks.json"), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
