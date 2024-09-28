package net.sodiumzh.nff.services.eventlisteners;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

@Cancelable
public class NFFTamedDeathEvent extends Event
{
	protected INFFTamed mob;
	protected DamageSource dmgSource;
	public NFFTamedDeathEvent(INFFTamed mob, DamageSource src)
	{
		this.mob = mob;
		dmgSource = src;
	}
	
	public INFFTamed getMob()
	{
		return mob;
	}
	
	public DamageSource getDamageSource()
	{
		return dmgSource;
	}
}
