package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Fired before a mob is calculated to be on a sun-burn tick.
 * <p> This event is cancelable. If canceled, this tick will not be considered as a sun-burn-tick.
 * <p> Note: it affects only {@link Mob#isSunBurnTick}. For sun-sensitive mobs not using {@link Mob#isSunBurnTick} you must manually add compatibility.
 */
@Cancelable
public class MobSunBurnTickEvent extends NFULivingEvent<Mob>
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
