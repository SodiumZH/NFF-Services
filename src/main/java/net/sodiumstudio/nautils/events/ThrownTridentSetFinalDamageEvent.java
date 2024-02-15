package net.sodiumstudio.nautils.events;

import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrownTridentSetFinalDamageEvent extends NaUtilsEntityEvent<ThrownTrident>
{
	private float dmg;
	private final float originalDmg;
	public ThrownTridentSetFinalDamageEvent(ThrownTrident entity, float originalDmg)
	{
		super(entity);
		this.dmg = originalDmg;
		this.originalDmg = originalDmg;
	}
	
	public float getDamage()
	{
		return dmg;
	}
	
	public void setDamage()
	{
		this.dmg = dmg;
	}
	
	public float getOriginalDamage()
	{
		return originalDmg;
	}
}
