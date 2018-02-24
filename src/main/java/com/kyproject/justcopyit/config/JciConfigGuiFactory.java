package com.kyproject.justcopyit.config;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JciConfigGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new JciConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class JciConfigGui extends GuiConfig {

        public JciConfigGui(GuiScreen parentScreen) {
            super(parentScreen, getConfigElements(), JustCopyIt.MODID, false, false, I18n.format("gui.config.main_title"));
        }

        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> list = new ArrayList<>();
            list.add(new DummyCategoryElement(I18n.format("gui.config.category.markers"), "gui.config.category.markers", CategoryEntryMarkers.class));
            list.add(new DummyCategoryElement(I18n.format("gui.config.category.energy"), "gui.config.category.energy", CategoryEntryEnergy.class));
            return list;
        }

        public static class CategoryEntryMarkers extends GuiConfigEntries.CategoryEntry {
            public CategoryEntryMarkers(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
                super(owningScreen, owningEntryList, configElement);
            }

            @Override
            protected GuiScreen buildChildScreen() {
                Configuration config = JciConfig.getConfig();
                ConfigElement categoryMarkers = new ConfigElement(config.getCategory(JciConfig.CATEGORY_NAME_MARKERS));
                List<IConfigElement> propertiesOnScreen = categoryMarkers.getChildElements();
                String windowTitle = I18n.format("gui.config.category.markers");

                return new GuiConfig(owningScreen, propertiesOnScreen, owningScreen.modID, this.configElement.requiresWorldRestart(), this.configElement.requiresMcRestart(), windowTitle);
            }
        }

        public static class CategoryEntryEnergy extends GuiConfigEntries.CategoryEntry {
            public CategoryEntryEnergy(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
                super(owningScreen, owningEntryList, configElement);
            }

            @Override
            protected GuiScreen buildChildScreen() {
                Configuration config = JciConfig.getConfig();
                ConfigElement categoryEnergy = new ConfigElement(config.getCategory(JciConfig.CATEGORY_NAME_ENERGY));
                List<IConfigElement> propertiesOnScreen = categoryEnergy.getChildElements();
                String windowTitle = I18n.format("gui.config.category.energy");

                return new GuiConfig(owningScreen, propertiesOnScreen, owningScreen.modID, this.configElement.requiresWorldRestart(), this.configElement.requiresMcRestart(), windowTitle);
            }
        }
    }
}
