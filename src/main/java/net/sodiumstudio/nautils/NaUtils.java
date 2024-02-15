package net.sodiumstudio.nautils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumstudio.befriendmobs.registry.BMItems;
import net.sodiumstudio.nautils.registries.NaUtilsItemRegistry;

public class NaUtils
{

	@Deprecated
	public static boolean isDebugMode = false;

	// TODO: change to "nautils" after separation
	public static final String MOD_ID = "befriendmobs";
	
	public NaUtils()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		NaUtilsItemRegistry.NAUTILS_ITEMS.register(modEventBus);
	}

	
}
