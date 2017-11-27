package com.kyproject.justcopyit.network;

import com.kyproject.justcopyit.tileentity.TileEntityExport;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MessageHandleGuiExportButton extends MessageXYZ<MessageHandleGuiExportButton> {
    private int id;
    private String name;

    public MessageHandleGuiExportButton(){}

    public MessageHandleGuiExportButton(TileEntityExport te, int id, String name){
        super(te);
        this.id = id;
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        id = buf.readInt();
        name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        buf.writeInt(id);
        ByteBufUtils.writeUTF8String(buf, name);
    }

    @Override
    public void handleClientSide(MessageHandleGuiExportButton message, EntityPlayer player){

    }

    @Override
    public void handleServerSide(MessageHandleGuiExportButton message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.world);
        if(te instanceof TileEntityExport) {
            ((TileEntityExport)te).buttonPressed(message.id, message.name);
        }
    }
}
