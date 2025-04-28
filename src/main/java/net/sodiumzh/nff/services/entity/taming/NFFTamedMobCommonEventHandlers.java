package net.sodiumzh.nff.services.entity.taming;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.mixin.events.entity.MobCheckDespawnEvent;
import net.sodiumzh.nautils.mixin.events.entity.MobSunBurnTickEvent;
import net.sodiumzh.nautils.mixin.events.entity.MonsterPreventSleepEvent;
import net.sodiumzh.nff.services.NFFServices;

/**
 * Common event listeners for {@link INFFTamed}.
 */
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFTamedMobCommonEventHandlers
{
	/**
	 * Make BMs not preventing sleep
	 */
	@SubscribeEvent
	public static void preventSleep(MonsterPreventSleepEvent event)
	{
		INFFTamed.ifTamed(event.getEntity(), bm -> {
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
		INFFTamed.ifTamed(event.getEntity(), bm -> event.setCanceled(true));
	}
	
	/**
	 * Handle sun immunity
	 */
	@SubscribeEvent
	public static void onMobSunBurnTick(MobSunBurnTickEvent event)
	{
		if (event.getEntity() instanceof INFFTamedSunSensitiveMob bssm && bssm.isSunImmune())
			event.setCanceled(true);
	}
}
