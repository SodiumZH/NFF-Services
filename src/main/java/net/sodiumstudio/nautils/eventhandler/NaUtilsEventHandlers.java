package net.sodiumstudio.nautils.eventhandler;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sodiumstudio.nautils.entity.ConditionalAttributeModifier;

public class NaUtilsEventHandlers {

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.START)
			ConditionalAttributeModifier.update();
	}
}
