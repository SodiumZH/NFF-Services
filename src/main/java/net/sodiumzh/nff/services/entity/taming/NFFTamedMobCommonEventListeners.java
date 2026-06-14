package net.sodiumzh.nff.services.entity.taming;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nfu.mixin.event.entity.MobCheckDespawnEvent;
import net.sodiumzh.nfu.mixin.event.entity.MobSunBurnTickEvent;
import net.sodiumzh.nfu.mixin.event.entity.MonsterPreventSleepEvent;

/**
 * Common event listeners for {@link INFFTamed}.
 */
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFTamedMobCommonEventListeners
{
	/**
	 * Make BMs not preventing sleep
	 */
	@SubscribeEvent
	public static void preventSleep(MonsterPreventSleepEvent event)
	{
		INFFTamed.ifBM(event.getEntity(), bm -> {
			if (bm.getOwnerUUID().equals(event.getPlayer().getUUID()) || bm.canPreventOtherPlayersSleep(event.getPlayer()))
				event.setCanceled(true);
		});
	}
	
	/**
	 * Make BMs not despawning
	 */
	@SubscribeEvent
	public static void checkDespawn(MobCheckDespawnEvent event)
	{
		INFFTamed.ifBM(event.getEntity(), bm -> event.setCanceled(true));
	}
	
	/**
	 * Handle sun immunity
	 */
	@SubscribeEvent
	public static void onMobSunBurnTick(MobSunBurnTickEvent event)
	{
		if (INFFTamed.get(event.getEntity()).map(t -> t.enableSunSensitivity() && t.isSunImmune()).orElse(false))
			event.setCanceled(true);
	}
}
