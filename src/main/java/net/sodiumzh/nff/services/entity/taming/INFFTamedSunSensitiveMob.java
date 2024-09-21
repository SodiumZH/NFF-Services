package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.Mob;
import net.sodiumzh.nautils.annotation.DontCallManually;
import net.sodiumzh.nautils.annotation.DontOverride;
import net.sodiumzh.nautils.function.MutablePredicate;
import net.sodiumzh.nautils.mixin.mixins.NaUtilsMixinMob;
import net.sodiumzh.nff.services.eventlisteners.NFFEntityEventListeners;
/**
 * This is an interface handling sun immunity for sun-sensitive mobs.
 * Put and remove entries in {@code sunImmuneConditions()} and {@sunImmuneNecessaryConditions()} to set rules.
 * Note: applying sun-immunity on mob must be manually implemented on each mob. 
 */
public interface INFFTamedSunSensitiveMob
{
	
	/**
	 * @deprecated Use {@code getBefriended} instead
	 */
	@DontOverride
	@Deprecated
	public default INFFTamed asBefriended()
	{
		return getBefriended();
	}
	
	@DontOverride
	public default INFFTamed getBefriended()
	{
		if (this instanceof INFFTamed bm)
			return bm;
		else throw new UnsupportedOperationException("INFFTamedSunSensitiveMob: mob missing INFFTamed interface.");
	}
	
	@DontOverride
	public default Mob getMob()
	{
		if (this instanceof Mob m)
			return m;
		else throw new UnsupportedOperationException("INFFTamedSunSensitiveMob: wrong object type, must be a mob.");
	}

	/**
	 * Check if the mob is immune to sun from rules.
	 * Implemented in {@link NFFEntityEventListeners#onMobSunBurnTick} via {@link NaUtilsMixinMob#isSunBurnTick}
	 */
	@DontOverride
	public default boolean isSunImmune()
	{
		return getSunImmunity().test(this);
	}
	
	/**
	 * Setup rules for sun immunity. Use {@code getSunImmunity()} to access rules.
	 * Called in EntityJoinWorldEvent only
	 */
	@DontCallManually
	public void setupSunImmunityRules();
	
	@DontOverride
	public default MutablePredicate<INFFTamedSunSensitiveMob> getSunImmunity()
	{
		return this.getBefriended().getData().getSunImmunity();
	}
	
}
