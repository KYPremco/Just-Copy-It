package com.kyproject.justcopyit.client;

import com.kyproject.justcopyit.client.gui.GuiBuilderContainer;
import com.kyproject.justcopyit.client.gui.GuiExportStructureContainer;
import com.kyproject.justcopyit.client.gui.GuiRemote;
import com.kyproject.justcopyit.client.gui.GuiScannerContainer;
import com.kyproject.justcopyit.container.builderContainer.ContainerBuilder;
import com.kyproject.justcopyit.container.exportStructureContainer.ContainerExportStructure;
import com.kyproject.justcopyit.container.scannercontainer.ContainerScanner;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import com.kyproject.justcopyit.tileentity.TileEntityExport;
import com.kyproject.justcopyit.tileentity.TileEntityScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int GUI_BUILDER_CONTAINER = 0, GUI_EXPORT_CONTAINER = 1, GUI_SCANNER_CONTAINER = 2, GUI_REMOTE = 3;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case GUI_BUILDER_CONTAINER:
                return new ContainerBuilder(player.inventory, (TileEntityBuilder) te);
            case GUI_EXPORT_CONTAINER:
                return new ContainerExportStructure(player.inventory, (TileEntityExport) te);
            case GUI_SCANNER_CONTAINER:
                return new ContainerScanner(player.inventory, (TileEntityScanner) te);
            default: return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case GUI_BUILDER_CONTAINER:
                return new GuiBuilderContainer(player.inventory, (TileEntityBuilder) te);
            case GUI_EXPORT_CONTAINER:
                return new GuiExportStructureContainer(player.inventory, (TileEntityExport) te);
            case GUI_SCANNER_CONTAINER:
                return new GuiScannerContainer(player.inventory, (TileEntityScanner) te);
            case GUI_REMOTE:
                return new GuiRemote(world, x,y,z);
            default: return null;
        }
    }
}
