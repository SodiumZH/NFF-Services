package net.sodiumstudio.befriendmobs.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.bmevents.setup.RegisterBefriendingTypeEvent;

@Mod.EventBusSubscriber(modid = BefriendMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BMSetupEvents {

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			ModLoader.get().postEvent(new RegisterBefriendingTypeEvent());
		});
	}
	
}
