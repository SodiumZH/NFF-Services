package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nautils.events.NaUtilsLivingEvent;

/**
 * Posted when a mob starts to check if it should despawn. This event will be always posted despite of the results of {@link Entity#shouldDespawnInPeaceful}, 
 * {@link Mob#requiresCustomPersistence} and {@link AllowDespawn} event.
 * <p>Cancellable. If cancelled, the whole despawn check will be skipped and this mob will not despawn, despite of the results above,
 * and {@link AllowDespawn} event will not be posted.
 */
@Cancelable
public class MobCheckDespawnEvent extends NaUtilsLivingEvent<Mob>
{

	public MobCheckDespawnEvent(Mob entity)
	{
		super(entity);
	}

}
