package com.kyproject.justcopyit.config;


import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JciConfig {

    private static Configuration config = null;

    public static final String CATEGORY_NAME_MARKERS = "markers", CATEGORY_NAME_ENERGY = "energy";

    public static int maxRangeMarker;

    public static double energyBase, energyMemorycardMultiplier;

    public static void preInit() {
        File configFile = new File(Loader.instance().getConfigDir(), "JustCopyIt.cfg");
        config = new Configuration(configFile);
        syncFromFiles();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }

    private static void syncFromFiles() {
        syncConfig(true, true);
    }

    private static void syncFromGui() {
        syncConfig(false, true);
    }

    public static void syncFromFields() {
        syncConfig(false,false);
    }

    private static void syncConfig(boolean loadFromConfigFile, boolean readFieldsFromConfig) {
        if(loadFromConfigFile) {
            config.load();
            config.save();
        }

        Property propertyMaxRangeMakers = config.get(CATEGORY_NAME_MARKERS, "marker_range", 64);
        propertyMaxRangeMakers.setLanguageKey("gui.config.markers.max_range.name");
        propertyMaxRangeMakers.setComment(I18n.format("gui.config.markers.max_range.comment"));
        propertyMaxRangeMakers.setMinValue(10);

        Property propertyEnergyBase = config.get(CATEGORY_NAME_ENERGY, "energy_base", 16D);
        propertyEnergyBase.setLanguageKey("gui.config.energy.energy_base.name");
        propertyEnergyBase.setComment(I18n.format("gui.config.energy.energy_base.comment"));
        propertyEnergyBase.setMinValue(1);

        Property propertyEnergyMemorycardMultiplier = config.get(CATEGORY_NAME_ENERGY, "energy_memorycard_multiplier", 1.5D);
        propertyEnergyMemorycardMultiplier.setLanguageKey("gui.config.energy.energy_memorycard_multiplier.name");
        propertyEnergyMemorycardMultiplier.setComment(I18n.format("gui.config.energy.energy_memorycard_multiplier.comment"));
        propertyEnergyMemorycardMultiplier.setMinValue(1);


        List<String> propertyMarkers = new ArrayList<>();
        propertyMarkers.add(propertyMaxRangeMakers.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_MARKERS, propertyMarkers);
        List<String> propertyEnergy = new ArrayList<>();
        propertyEnergy.add(propertyEnergyBase.getName());
        propertyEnergy.add(propertyEnergyMemorycardMultiplier.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_ENERGY, propertyEnergy);

        if(readFieldsFromConfig) {
            maxRangeMarker = propertyMaxRangeMakers.getInt();
            energyBase = propertyEnergyBase.getDouble();
            energyMemorycardMultiplier = propertyEnergyMemorycardMultiplier.getDouble();
        }

        propertyMaxRangeMakers.set(maxRangeMarker);
        propertyEnergyBase.set(energyBase);
        propertyEnergyMemorycardMultiplier.set(energyMemorycardMultiplier);

        if(config.hasChanged()) {
            config.save();
        }
    }

    public static class ConfigEventHandler {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(JustCopyIt.MODID)) {
                syncFromGui();
            }
        }
    }

}
