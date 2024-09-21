package net.sodiumzh.nautils;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;
import net.sodiumzh.nautils.registries.NaUtilsEntityDataSerializers;
import net.sodiumzh.nautils.registries.NaUtilsItemRegistry;

@Mod(NaUtils.MOD_ID)
public class NaUtils
{
	
	// TODO: change to "nautils" after separation
	public static final String MOD_ID = "nautils";
	@Deprecated
	public static final String MOD_ID_FINAL = MOD_ID;
	private static MinecraftServer server = null;
	
	public NaUtils()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NaUtilsConfigs.CONFIG);
		NaUtilsItemRegistry.NAUTILS_ITEMS.register(modEventBus);
		NaUtilsEntityDataSerializers.SERIALIZERS.register(modEventBus);
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
