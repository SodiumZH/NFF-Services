package net.sodiumzh.nautils.capability;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.mixin.events.entity.EntityTickEvent;

/**
 * Event listeners for NaUtils capability implementation.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaUtilsCapsEventListeners
{
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent event)
	{
		for (var cap: CEntityTickingCapability.ALL_CAPS)
		{
			event.getEntity().getCapability(cap).ifPresent(CEntityTickingCapability::tick);
		}
	}
}
