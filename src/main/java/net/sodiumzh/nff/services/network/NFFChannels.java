package net.sodiumzh.nff.services.network;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sodiumzh.nautils.statics.NaUtilsNetworkStatics;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFFChannels {
	public static SimpleChannel BM_CHANNEL;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
    	
    	BM_CHANNEL = NaUtilsNetworkStatics.newChannel(NFFServices.MOD_ID, "bm_channel", VERSION);
    	NaUtilsNetworkStatics.registerDefaultClientGamePacket(nextID(), BM_CHANNEL, ClientboundNFFGUIOpenPacket.class);
    	NaUtilsNetworkStatics.registerDefaultClientGamePacket(nextID(), BM_CHANNEL, ClientboundTamedInitPacket.class);
    	NaUtilsNetworkStatics.registerDefaultClientGamePacket(nextID(), BM_CHANNEL, CNFFTamedCommonData.ClientboundDataSyncPacket.class);
    	/*BM_CHANNEL = NetworkRegistry.newSimpleChannel(
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
    	BM_CHANNEL.registerMessage(
                nextID(),
                ClientboundNFFGUIOpenPacket.class,
                (pack, buffer) -> {
                    pack.write(buffer);
                },
                (buffer) -> {
                    return new ClientboundNFFGUIOpenPacket(buffer);
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
