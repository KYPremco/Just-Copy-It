package com.kyproject.justcopyit.init;
import com.kyproject.justcopyit.tileentity.TileEntityWorldMarker;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {

    public static void init() {
        GameRegistry.registerTileEntity(TileEntityBuilder.class, "kyp_BlockBuilder");
        GameRegistry.registerTileEntity(TileEntityWorldMarker.class, "kyp_BlockWorldMarker");
    }

}
