package com.kyproject.justcopyit.templates;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

public class StructureTemplate {

    private static ArrayList<String> blockItemFilter = new ArrayList<>();

    private ArrayList<BlockPlace.BlockState> blockLayer = new ArrayList<>();
    private ArrayList<BlockPlace.BlockState> blockLayerTop = new ArrayList<>();
    private ArrayList<BlockPlace.BlockState> liquidLayer = new ArrayList<>();

    private ArrayList<BlockPlace.BlockState> blocks = new ArrayList<>();

    private BlockPlace structure;


    public void addLayer(String layer, int x, int y, int z, IBlockState state, World world, BlockPos blockPos) {
        switch (layer) {
            case "blockLayer":
                this.setLayer(x, y, z, state, world, blockPos);
                break;
            case "liquid":
                liquidLayer.add(new BlockPlace.BlockState(x,y,z,state, null));
                break;
            default:
                break;
        }
    }

    private void setLayer(int x, int y, int z, IBlockState state, World world, BlockPos blockPos) {
        boolean inFilter = false;
        for(String blockString : blockItemFilter) {
            if(blockString != null) {
                Block blockInFilter = Block.getBlockFromName(blockString);
                if(blockInFilter != null) {
                    if(blockInFilter.equals(state.getBlock())) {
                        blockLayerTop.add(new BlockPlace.BlockState(x,y,z,state, null));
                        inFilter = true;
                        break;
                    }
                }
            }
        }

        if(!inFilter) {

            TileEntity tileentity = world.getTileEntity(blockPos.add(x,y,z));
            if(tileentity != null){
                NBTTagCompound tagCompound = tileentity.writeToNBT(new NBTTagCompound());

                Set<String> tagKeys = tagCompound.getKeySet();
                for(String s : tagKeys) {
                    String searchInsideKeys = tagCompound.getTag(s).toString();

                    if(searchInsideKeys.toLowerCase().contains("item") || s.toLowerCase().contains("item")) {
                        tagCompound.removeTag(s);
                        break;
                    }
                }
                for(String s : tagKeys) {
                    String searchInsideKeys = tagCompound.getTag(s).toString();

                    if(searchInsideKeys.toLowerCase().contains("energy") || s.toLowerCase().contains("energy")) {
                        tagCompound.removeTag(s);
                        break;
                    }
                }

                tagCompound.removeTag("x");
                tagCompound.removeTag("y");
                tagCompound.removeTag("z");

                blockLayerTop.add(new BlockPlace.BlockState(x,y,z,state, tagCompound));
            } else {
                blockLayer.add(new BlockPlace.BlockState(x,y,z,state, null));
            }
        }
    }

    public void add(int x, int y, int z, IBlockState state, @Nullable NBTTagCompound nbtTagCompound) {
        blocks.add(new BlockPlace.BlockState(x,y,z,state, nbtTagCompound));
    }

    public void combine() {
        blocks.addAll(blockLayer);
        blocks.addAll(blockLayerTop);
        blocks.addAll(liquidLayer);
    }

    public BlockPlace getStructure() {
        return structure;
    }

    public void create(String type, String name, EnumFacing facing, int durability, int rangeX, int rangeY, int rangeZ) {
        structure = new BlockPlace(type, name, facing, durability, rangeX, rangeY, rangeZ, this.blocks);
    }

    public void loadBlockItemFilter() {
        try {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            blockItemFilter = new Gson().fromJson(new FileReader("resources\\JustCopyIt\\layerFilter.json"), type);
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
        public int durability;
        public int rangeX;
        public int rangeY;
        public int rangeZ;
        public ArrayList<BlockState> blocks;

        private BlockPlace(String type, String name, EnumFacing facing, int durability, int rangeX, int rangeY, int rangeZ, ArrayList<BlockState> blocks) {
            this.type = type;
            this.name = name;
            this.facing = facing;
            this.blocks = blocks;
            this.rangeX = rangeX;
            this.rangeY = rangeY;
            this.rangeZ = rangeZ;
            this.durability = durability;
        }

        public static class BlockState {
            public int x;
            public int y;
            public int z;
            public IBlockState state;
            public NBTTagCompound nbtTagCompound;

            private BlockState(int x, int y, int z, IBlockState state, @Nullable NBTTagCompound nbtTagCompound) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.state = state;
                this.nbtTagCompound = nbtTagCompound;
            }
        }
    }
}
