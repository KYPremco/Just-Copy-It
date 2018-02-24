package com.kyproject.justcopyit.init;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kyproject.justcopyit.JustCopyIt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Filters {

    private ArrayList<changeItemFilter> changeItemFilter = new ArrayList<>();

    private ArrayList<String> layerFilter = new ArrayList<>();

    private ArrayList<String> itemBlacklist = new ArrayList<>();

    public Filters() {
        this.createItemFilter();
        this.createLayerFilter();
        this.createItemBlacklist();
    }

    public void createFilter()  {
        File itemFilterFile = new File("resources\\JustCopyIt\\changeItemFilter.json");
        File layerFilterFile = new File("resources\\JustCopyIt\\layerFilter.json");
        File itemBlackList = new File("resources\\JustCopyIt\\itemBlacklist.json");

        if(!itemFilterFile.exists()) {
            Gson gson = new Gson();
            try {
                FileWriter writer = new FileWriter("resources\\JustCopyIt\\changeItemFilter.json");
                writer.write(gson.toJson(changeItemFilter));
                writer.close();
            } catch (IOException e) {
                JustCopyIt.logger.warn(e);
            }
        }

        if(!layerFilterFile.exists()) {
            Gson gson = new Gson();
            try {
                FileWriter writer = new FileWriter("resources\\JustCopyIt\\layerFilter.json");
                writer.write(gson.toJson(layerFilter));
                writer.close();
            } catch (IOException e) {
                JustCopyIt.logger.warn(e);
            }
        }

        if(!itemBlackList.exists()) {
            Gson gson = new Gson();
            try {
                FileWriter writer = new FileWriter("resources\\JustCopyIt\\itemBlacklist.json");
                writer.write(gson.toJson(this.itemBlacklist));
                writer.close();
            } catch (IOException e) {
                JustCopyIt.logger.warn(e);
            }
        }

    }

    private void createItemFilter() {
        changeItemFilter.add(new changeItemFilter("minecraft:farmland", "minecraft:dirt"));
        changeItemFilter.add(new changeItemFilter("minecraft:grass", "minecraft:dirt"));
    }

    private void createLayerFilter() {
        layerFilter.add("minecraft:torch");
        layerFilter.add("minecraft:ladder");
        layerFilter.add("minecraft:wheat");
        layerFilter.add("minecraft:melon_stem");
        layerFilter.add("minecraft:beetroots");
        layerFilter.add("minecraft:nether_wart");
        layerFilter.add("minecraft:carrots");
        layerFilter.add("minecraft:potatoes");
    }

    private void createItemBlacklist() {
        itemBlacklist.add("minecraft:double_grass");
    }

    public static class changeItemFilter {
        public String original;
        public String replace;

        public changeItemFilter(String original, String replace) {
            this.original = original;
            this.replace = replace;
        }
    }

    public ArrayList<Filters.changeItemFilter> readJsonFilter() {
        try {
            Type type = new TypeToken<ArrayList<Filters.changeItemFilter>>(){}.getType();
            return new Gson().fromJson(new FileReader("resources\\JustCopyIt\\changeItemFilter.json"), type);
        } catch (IOException e) {
            JustCopyIt.logger.error(e);
        }
        return null;
    }

    public ArrayList<String> readJsonBlacklist() {
        try {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            return new Gson().fromJson(new FileReader("resources\\JustCopyIt\\itemBlacklist.json"), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
