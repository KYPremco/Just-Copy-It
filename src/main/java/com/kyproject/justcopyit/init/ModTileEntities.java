package com.kyproject.justcopyit.init;
import com.kyproject.justcopyit.tileentity.TileEntityExport;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {

    public static void init() {
        //Creative
        GameRegistry.registerTileEntity(TileEntityExport.class, "kypjci_BlockExportStructure");

        //Survival
        GameRegistry.registerTileEntity(TileEntityBuilder.class, "kypjci_BlockBuilder");
        GameRegistry.registerTileEntity(TileEntityWorldMarker.class, "kypjci_BlockWorldMarker");
    }

}
