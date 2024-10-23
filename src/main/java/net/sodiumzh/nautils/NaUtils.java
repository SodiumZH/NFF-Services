package net.sodiumzh.nautils;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumzh.nautils.registries.*;
import net.sodiumzh.nautils.network.NaUtilsDataSerializers;
import org.apache.logging.log4j.core.net.Priority;

import javax.annotation.Nullable;

@Mod(NaUtils.MOD_ID)
public class NaUtils {

	// TODO: change to "nautils" after separation
	public static final String MOD_ID = "nautils";
	@Deprecated
	public static final String MOD_ID_FINAL = MOD_ID;
	private static MinecraftServer server = null;

	public NaUtils() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NaUtilsConfigs.CONFIG);
		NaUtilsItemRegistry.NAUTILS_ITEMS.register(modEventBus);
		NaUtilsEntityDataSerializers.SERIALIZERS.register(modEventBus);

		// Custom registry related
		NaUtilsRegistries.init();
		NaUtilsDataSerializers.SERIALIZERS.merge();
	}

	/**
	 * Get the server instance if it's on server. On other threads/side or if the server isn't open,
	 * return null.
	 */
	@Nullable
	public static MinecraftServer getServer() {
		if (server == null) return null;
		return server.isSameThread() ? server : null;
	}

	@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeEventListeners {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void onServerAboutToStart(ServerAboutToStartEvent event) {
			server = event.getServer();
		}

		@SubscribeEvent
		public static void onServerStopped(ServerStoppedEvent event) {
			server = null;
		}
	}
	
}
