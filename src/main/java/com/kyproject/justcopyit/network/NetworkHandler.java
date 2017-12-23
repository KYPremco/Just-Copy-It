package com.kyproject.justcopyit.network;

import com.kyproject.justcopyit.JustCopyIt;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper INSTANCE;

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(JustCopyIt.MODID);

        INSTANCE.registerMessage(MessageHandleGuiBuilderButton.class, MessageHandleGuiBuilderButton.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageHandleGuiExportButton.class, MessageHandleGuiExportButton.class, 1, Side.SERVER);
    }

    public static void sendToServer(IMessage message){
        INSTANCE.sendToServer(message);;
    }

}
