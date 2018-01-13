package com.kyproject.justcopyit.init;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.item.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import static com.kyproject.justcopyit.util.InjectionUtil.Null;

@ObjectHolder(JustCopyIt.MODID)
public class ModItems {

        public static final ItemBlueprint BLUEPRINT = Null();
        public static final ItemBlueprintCreative BLUEPRINT_CREATIVE = Null();
        public static final ItemBinder MARK_BINDER = Null();
        public static final ItemMagicStick MAGIC_STICK = Null();
        public static final ItemSpeedUpgrade UPGRADE_SPEED = Null();
        public static final ItemLaser ITEM_LASER = Null();
        public static final ItemMemory ITEM_MEMORY = Null();
        public static final ItemProcessor ITEM_PROCESSOR = Null();
        public static final ItemLens ITEM_LENS = Null();
        public static final ItemUpgradeEmpty UPGRADE_EMPTY = Null();
        public static final ItemMemoryUpgrade UPGRADE_MEMORY = Null();
        public static final ItemRemote REMOTE = Null();


    @Mod.EventBusSubscriber()
    public static class RegistryHandler {
        private static final Item[] ITEMS = {
                new ItemBlueprint("blueprint"),
                new ItemBlueprintCreative("blueprint_creative"),
                new ItemBinder("mark_binder"),
                new ItemMagicStick("magic_stick"),
                new ItemSpeedUpgrade("upgrade_speed"),
                new ItemLaser("laser"),
                new ItemMemory("memory"),
                new ItemProcessor("processor"),
                new ItemLens("lens"),
                new ItemUpgradeEmpty("upgrade_empty"),
                new ItemMemoryUpgrade("upgrade_memory"),
                new ItemRemote("remote")
        };

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(ITEMS);

        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            for (Item item: ITEMS)
            {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }

    }

}
