package net.sodiumstudio.nautils.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired before a mob is calculated to be on a sun-burn tick.
 * <p> This event is cancelable. If canceled, this tick will not be considered as a sun-burn-tick.
 * <p> Note: it affects only {@link Mob#isSunBurnTick}. For sun-sensitive mobs not using {@link Mob#isSunBurnTick} you must manually add compatibility.
 */
@Cancelable
public class MobSunBurnTickEvent extends NaUtilsLivingEvent<Mob>
{
	public MobSunBurnTickEvent(Mob entity)
	{
		super(entity);
	}
	
	@Override
	public Mob getEntity()
	{
		return (Mob)(super.getEntity());
	}

}
