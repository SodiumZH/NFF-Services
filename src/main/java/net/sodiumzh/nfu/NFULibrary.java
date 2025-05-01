package net.sodiumzh.nfu;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.network.NFUDataSerializers;
import net.sodiumzh.nfu.registry.NFUConfigs;
import net.sodiumzh.nfu.registry.NFUEntityDataSerializers;
import net.sodiumzh.nfu.registry.NFUItems;
import net.sodiumzh.nfu.registry.NFURegistries;

import javax.annotation.Nullable;

@Mod(NFULibrary.MOD_ID_LEGACY)
public class NFULibrary {

	// TODO: change to "nautils" after separation
	public static final String MOD_ID_LEGACY = "nautils";
	@Deprecated
	public static final String MOD_ID_FINAL = MOD_ID_LEGACY;
	private static MinecraftServer server = null;

	public NFULibrary() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NFUConfigs.CONFIG);
		NFUItems.NAUTILS_ITEMS.register(modEventBus);
		NFUEntityDataSerializers.SERIALIZERS.register(modEventBus);

		// Custom registry related
		NFURegistries.init();
		mergeCustomRegistries();
	}

	private void mergeCustomRegistries()
	{
		NFUDataSerializers.SERIALIZERS.merge();
		MobAngerReason.REASONS.merge();
		MobAngerRules.RULES.merge();
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

	private void portSaveDataKeys() {

	}

	@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID_LEGACY, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeEventListeners {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void onServerAboutToStart(ServerAboutToStartEvent event) {
			server = event.getServer();
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void onServerStopped(ServerStoppedEvent event) {
			server = null;
		}
	}
	
}
