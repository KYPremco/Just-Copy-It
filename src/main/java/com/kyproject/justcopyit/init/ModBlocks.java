package com.kyproject.justcopyit.init;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.kyproject.justcopyit.util.InjectionUtil.Null;

@GameRegistry.ObjectHolder(JustCopyIt.MODID)
public class ModBlocks {

    //Creative
    public static final BlockExportStructure EXPORT_STRUCTURE = Null();

    // Survival
    public static final BlockStructureBuilder STRUCTURE_BUILDER = Null();
    public static final BlockMarkerMaster WORLD_MARKER_MASTER = Null();
    public static final BlockMarker WORLD_MARKER = Null();
    public static final BlockStructureScanner STRUCTURE_SCANNER = Null();

    @Mod.EventBusSubscriber(modid = JustCopyIt.MODID)
    public static class RegistrationHandler {

        public static final Block[] BLOCKS = {
                //Creative
                new BlockExportStructure("export_structure", Material.ROCK),

                // Survival
                new BlockStructureBuilder("structure_builder", Material.ROCK),
                new BlockMarkerMaster("world_marker_master", Material.ROCK),
                new BlockMarker("world_marker", Material.ROCK),
                new BlockStructureScanner("structure_scanner", Material.ROCK)
        };

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(BLOCKS);
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
              for(Block block : BLOCKS) {
                event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName().toString()));
            }
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            for (Block block: BLOCKS)
            {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
            }
        }

    }

}
