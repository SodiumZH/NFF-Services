package net.sodiumzh.nff.services.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.client.gui.screen.NFFTamedGUI;

@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFClientEventListeners 
{

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		
		if (event.side == LogicalSide.CLIENT)
		{
			@SuppressWarnings("resource")
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen != null && mc.screen instanceof NFFTamedGUI bgs)
			{
				if (!bgs.mob.asMob().isAlive() || !bgs.mob.asMob().isAddedToWorld())
				{
					mc.setScreen(null);
				}
				
			}
		}
	}

}
