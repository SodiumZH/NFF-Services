package net.sodiumstudio.nautils;

import com.mojang.logging.LogUtils;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumstudio.befriendmobs.registry.BMItems;
import net.sodiumstudio.nautils.registries.NaUtilsItemRegistry;

public class NaUtils
{

	@Deprecated
	public static boolean isDebugMode = false;

	// TODO: change to "nautils" after separation
	public static final String MOD_ID = "befriendmobs";
	private static MinecraftServer server;
	
	public NaUtils()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		NaUtilsItemRegistry.NAUTILS_ITEMS.register(modEventBus);
	}

	public static MinecraftServer getServer()
	{
		return server;
	}
	
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        server = event.getServer();
    }
    
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event)
    {
    	server = null;
    }
	
	
}
