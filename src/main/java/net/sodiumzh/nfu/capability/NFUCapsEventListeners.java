package net.sodiumzh.nfu.capability;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.mixin.event.entity.EntityTickEvent;

/**
 * Event listeners for NFU capability implementation.
 */
@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFUCapsEventListeners
{
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent event)
	{
		for (var cap: CEntityTickingCapability.ALL_CAPS)
		{
			event.getEntity().getCapability(cap).ifPresent(c -> {
				if (c.getTickingSide().isCorrectSide(event.getEntity().level.isClientSide)) {
					c.tick();
					if (c instanceof CEntityTimerCapability<?> timer) {
						timer.tickTimer();
					}
				}
			});
		}
	}
}
