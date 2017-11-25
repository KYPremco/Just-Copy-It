package com.kyproject.justcopyit.network;

import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageHandleGuiBuilderButton extends MessageXYZ<MessageHandleGuiBuilderButton> {
    private int id;

    public MessageHandleGuiBuilderButton(){}

    public MessageHandleGuiBuilderButton(TileEntityBuilder te, int id){
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
    public void handleClientSide(MessageHandleGuiBuilderButton message, EntityPlayer player){

    }

    @Override
    public void handleServerSide(MessageHandleGuiBuilderButton message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.world);
        if(te instanceof TileEntityBuilder) {
            ((TileEntityBuilder)te).buttonPressed(message.id);
        }
    }
}
