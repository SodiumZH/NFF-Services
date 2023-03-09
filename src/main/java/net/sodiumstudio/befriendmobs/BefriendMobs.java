package net.sodiumstudio.befriendmobs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sodiumstudio.befriendmobs.example.EXAMPLE_EntityTypeRegister;
import net.sodiumstudio.befriendmobs.registry.RegItems;

// This class will be the mod main class of future Befriending Mob API library.

@Mod(BefriendMobs.MOD_ID)
public class BefriendMobs {

	public static final String MOD_ID = "befriendmobs";
	public static String modDomain()
	{
		return "befriendmobs";	// Temporary for locating resource
	}
	
	public BefriendMobs()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		RegItems.ITEMS.register(modEventBus);
		EXAMPLE_EntityTypeRegister.EXAMPLE_ENTITY_TYPES.register(modEventBus);
		
        MinecraftForge.EVENT_BUS.register(this);
	}	
	
	
	
    public static final boolean IS_DEBUG_MODE = true;
	
}
