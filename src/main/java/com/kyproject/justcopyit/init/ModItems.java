package com.kyproject.justcopyit.init;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.item.ItemBinder;
import com.kyproject.justcopyit.item.ItemMemoryCard;
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

        public static final ItemMemoryCard MEMORY_CARD = Null();

        public static final ItemBinder MARK_BINDER = null;


    @Mod.EventBusSubscriber()
    public static class RegistryHandler {

        public static final Item[] ITEMS = {
                new ItemMemoryCard("memory_card"),
                new ItemBinder("mark_binder")
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
