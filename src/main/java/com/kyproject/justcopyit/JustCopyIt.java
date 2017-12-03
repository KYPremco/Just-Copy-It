package com.kyproject.justcopyit;

import com.kyproject.justcopyit.client.GuiHandler;
import com.kyproject.justcopyit.init.ModTileEntities;
import com.kyproject.justcopyit.network.NetworkHandler;
import com.kyproject.justcopyit.proxy.CommonProxy;
import com.kyproject.justcopyit.tab.CreativeTabJustCopyIt;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = JustCopyIt.MODID, version = JustCopyIt.VERSION, name = JustCopyIt.NAME, acceptableSaveVersions = "[1.12.2]")
public class JustCopyIt {
    public static final String MODID = "kypjci";
    public static final String VERSION = "0.7";
    public static final String NAME = "Just Copy It";

    public static Logger logger;

    @SidedProxy(clientSide = "com.kyproject.justcopyit.proxy.ClientProxy", serverSide = "com.kyproject.justcopyit.proxy.CommonProxy")
    public  static CommonProxy proxy;

    @Mod.Instance
    public static JustCopyIt instance;

    public static CreativeTabJustCopyIt creativeTabJustCopyIt;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        boolean mkdirs = new File("resources/JustCopyIt/structures").mkdirs();
        if(mkdirs) {
            logger.info("Structure directory created");
        }
        creativeTabJustCopyIt = new CreativeTabJustCopyIt(CreativeTabs.getNextID(), "tab_JustCopyIt");
        ModTileEntities.init();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        proxy.init(event);

        NetworkHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
