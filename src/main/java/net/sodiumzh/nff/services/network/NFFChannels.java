package net.sodiumzh.nff.services.network;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sodiumzh.nfu.util.NFUNetworkStatics;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFFChannels {
	public static SimpleChannel CHANNEL;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
    	
    	CHANNEL = NFUNetworkStatics.newChannel(NFFServices.MOD_ID, "nff_channel", VERSION);
    	NFUNetworkStatics.registerDefaultClientGamePacket(nextID(), CHANNEL, ClientboundNFFGuiOpenPacket.class);
    	NFUNetworkStatics.registerDefaultClientGamePacket(nextID(), CHANNEL, ClientboundNFFTamedInitPacket.class);
    	NFUNetworkStatics.registerDefaultClientGamePacket(nextID(), CHANNEL, CNFFTamedCommonData.ClientboundDataSyncPacket.class);
    	/*CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(NFFServices.MOD_ID, "bm_channel"),
                () -> {
                    return VERSION;
                },
                (version) -> {
                    return version.equals(VERSION);
                },
                (version) -> {
                    return version.equals(VERSION);
                });
    	CHANNEL.registerMessage(
                nextID(),
                ClientboundNFFGuiOpenPacket.class,
                (pack, buffer) -> {
                    pack.write(buffer);
                },
                (buffer) -> {
                    return new ClientboundNFFGuiOpenPacket(buffer);
                },
                (pack, ctx) -> {
                	ctx.get().enqueueWork(() ->
                	{ 
                		pack.handle(Minecraft.getInstance().getConnection());
                	});
                	ctx.get().setPacketHandled(true);
                }
        );*/
    }
    
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
    	event.enqueueWork(() -> 
    	{
    		registerMessage();
    	});
    }
    
}
