package com.kyproject.justcopyit.network;

import com.kyproject.justcopyit.tileentity.TileEntityScanner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageHandleGuiScannerButton extends MessageXYZ<MessageHandleGuiScannerButton> {
    private int id;
    private boolean op;

    public MessageHandleGuiScannerButton(){}

    public MessageHandleGuiScannerButton(TileEntityScanner te, int id, boolean op){
        super(te);
        this.id = id;
        this.op = op;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        id = buf.readInt();
        op = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        buf.writeInt(id);
        buf.writeBoolean(op);
    }

    @Override
    public void handleClientSide(MessageHandleGuiScannerButton message, EntityPlayer player){

    }

    @Override
    public void handleServerSide(MessageHandleGuiScannerButton message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.world);
        if(te instanceof TileEntityScanner) {
            ((TileEntityScanner)te).buttonPressed(message.id, message.op);
        }
    }
}
