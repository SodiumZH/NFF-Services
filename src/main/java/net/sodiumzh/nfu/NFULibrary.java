package net.sodiumzh.nfu;

import net.minecraft.resources.ResourceLocation;
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
import net.sodiumzh.nfu.item.bauble.BaubleEquippingConditions;
import net.sodiumzh.nfu.item.bauble.NFUBaubleAPI;
import net.sodiumzh.nfu.network.NFUDataSerializers;
import net.sodiumzh.nfu.registry.*;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirector;

import javax.annotation.Nullable;

@Mod(NFULibrary.MOD_ID)
public class NFULibrary {

	public static final String MOD_ID = "nfulib";
	public static final String MOD_ID_LEGACY = "nautils";
	private static MinecraftServer server = null;

	public NFULibrary() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NFUConfigs.CONFIG);
		NFUItems.NAUTILS_ITEMS.register(modEventBus);
		NFUEntityDataSerializers.SERIALIZERS.register(modEventBus);
		NFUEntityTypes.ENTITY_TYPES.register(modEventBus);


		// Custom registry related
		NFURegistries.init();
		mergeCustomRegistries();
		portSaveDataKeys();
		NFUBaubleAPI.init();
	}

	private void mergeCustomRegistries()
	{
		NFUDataSerializers.SERIALIZERS.merge();
		MobAngerReason.REASONS.merge();
		MobAngerRules.RULES.merge();
		BaubleEquippingConditions.CONDITION_REGISTRY_COLLECTION.merge();
	}


	/**
	 * Get the server instance if it's on server. On other threads/side or if the server isn't open,
	 * return null.
	 */
	@Nullable
	@Deprecated
	public static MinecraftServer getServer() {
		if (server == null) return null;
		return server.isSameThread() ? server : null;
	}

	private void portSaveDataKeys() {
		SaveDataLocationRedirector.get()
			.redirectNamespace(MOD_ID_LEGACY, MOD_ID)
			.redirectEntityCapability(new ResourceLocation("nffservices", "cap_bauble_equippable_mob"), new ResourceLocation(NFULibrary.MOD_ID, "cap_bauble_equippable_mob"));
	}

	@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
