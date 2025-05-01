package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.projectile.ThrownTrident;
import net.sodiumzh.nfu.event.NFUEntityEvent;

public class ThrownTridentSetBaseDamageEvent extends NFUEntityEvent<ThrownTrident>
{
	private float dmg;
	private final float originalDmg;
	public ThrownTridentSetBaseDamageEvent(ThrownTrident entity, float originalDmg)
	{
		super(entity);
		this.dmg = originalDmg;
		this.originalDmg = originalDmg;
	}
	
	public float getDamage()
	{
		return dmg;
	}
	
	public void setDamage(float value)
	{
		this.dmg = value;
	}
	
	public float getOriginalDamage()
	{
		return originalDmg;
	}
}
