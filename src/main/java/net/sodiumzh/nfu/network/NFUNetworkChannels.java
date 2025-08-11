package net.sodiumzh.nfu.network;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.network.packet.ClientboundEntityMotionUpdatePacket;
import net.sodiumzh.nfu.util.NFUNetworkStatics;

@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFUNetworkChannels {
    public static SimpleChannel CHANNEL;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        CHANNEL = NFUNetworkStatics.newChannel(NFULibrary.MOD_ID, "nfu_channel", VERSION);
        NFUNetworkStatics.registerDefaultClientGamePacket(nextID(), CHANNEL, ClientboundEntityMotionUpdatePacket.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NFUNetworkChannels::registerMessage);
    }
}
