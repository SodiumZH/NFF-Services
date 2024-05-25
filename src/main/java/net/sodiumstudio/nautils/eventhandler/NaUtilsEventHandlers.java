package net.sodiumstudio.nautils.eventhandler;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.events.ServerEntityTickEvent;
import net.sodiumstudio.nautils.entity.ConditionalAttributeModifier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaUtilsEventHandlers {

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.START)
			ConditionalAttributeModifier.update();
	}

}
