package com.kyproject.justcopyit.network;

import com.kyproject.justcopyit.tileentity.TileEntityScanner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageHandleGuiScannerButton extends MessageXYZ<MessageHandleGuiScannerButton> {
    private int id;

    public MessageHandleGuiScannerButton(){}

    public MessageHandleGuiScannerButton(TileEntityScanner te, int id){
        super(te);
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        buf.writeInt(id);
    }

    @Override
    public void handleClientSide(MessageHandleGuiScannerButton message, EntityPlayer player){

    }

    @Override
    public void handleServerSide(MessageHandleGuiScannerButton message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.world);
        if(te instanceof TileEntityScanner) {
            ((TileEntityScanner)te).buttonPressed(message.id);
        }
    }
}
