package com.kyproject.justcopyit.proxy;

import com.kyproject.justcopyit.block.render.RenderAreaLine;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWorldMarker.class, new RenderAreaLine());
    }


    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
