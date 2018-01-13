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

    public Filters() {
        this.createItemFilter();
        this.createLayerFilter();
    }

    public void createFilter()  {
        File itemFilterFile = new File("resources\\JustCopyIt\\changeItemFilter.json");
        File layerFilterFile = new File("resources\\JustCopyIt\\layerFilter.json");

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

    }

    private void createItemFilter() {
        changeItemFilter.add(new changeItemFilter("minecraft:farmland", "minecraft:dirt"));
        changeItemFilter.add(new changeItemFilter("minecraft:grass", "minecraft:dirt"));
        changeItemFilter.add(new changeItemFilter("minecraft:wheat", "minecraft:wheat_seeds"));
        changeItemFilter.add(new changeItemFilter("minecraft:melon_stem", "minecraft:melon_seeds"));
        changeItemFilter.add(new changeItemFilter("minecraft:beetroots", "minecraft:beetroot_seeds"));
        changeItemFilter.add(new changeItemFilter("minecraft:nether_wart", "minecraft:nether_wart_seeds"));
        changeItemFilter.add(new changeItemFilter("minecraft:carrots", "minecraft:carrot"));
        changeItemFilter.add(new changeItemFilter("minecraft:potatoes", "minecraft:potato"));
    }

    private void createLayerFilter() {
        layerFilter.add("minecraft:torch");
        layerFilter.add("minecraft:ladder");
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

}
