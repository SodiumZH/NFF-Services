package net.sodiumzh.nff.services.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.event.client.RegisterGuiScreenEvent;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NFFClientSetupEventListeners 
{

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			ModLoader.get().postEvent(new RegisterGuiScreenEvent());
		});
	}
	
}
