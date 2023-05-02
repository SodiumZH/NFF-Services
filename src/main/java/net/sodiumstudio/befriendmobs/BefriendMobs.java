package net.sodiumstudio.befriendmobs;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumstudio.befriendmobs.registry.BefMobItems;

// This class will be the mod main class of future Befriending Mob API library.

//@Mod(....)
public class BefriendMobs {

	public static final String MOD_ID = "befriendmobs";
	//public static final String MOD_ID = "befriendmobs";
	public static String modDomain()
	{
		return "befriendmobs";	// Temporary for locating resource
	}
	
	public BefriendMobs()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		//modEventBus.addListener(this::commonSetup);
		
		BefMobItems.ITEMS.register(modEventBus);
		//EXAMPLE_EntityTypeRegister.EXAMPLE_ENTITY_TYPES.register(modEventBus);
		
        //MinecraftForge.EVENT_BUS.register(this);
	}	
	
	// On debug set it true. On publication set it false to block verbose debug output.
    public static final boolean IS_DEBUG_MODE = true;
	
}
