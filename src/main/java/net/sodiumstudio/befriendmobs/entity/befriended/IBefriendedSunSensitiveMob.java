package net.sodiumstudio.befriendmobs.entity.befriended;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.entity.Mob;
import net.sodiumstudio.nautils.annotation.DontCallManually;
import net.sodiumstudio.nautils.annotation.DontOverride;
import net.sodiumstudio.nautils.function.MutablePredicate;

/**
 * This is an interface handling sun immunity for sun-sensitive mobs.
 * Put and remove entries in {@code sunImmuneConditions()} and {@sunImmuneNecessaryConditions()} to set rules.
 * Note: applying sun-immunity on mob must be manually implemented on each mob. 
 */
public interface IBefriendedSunSensitiveMob
{
	
	/**
	 * @deprecated Use {@code getBefriended} instead
	 */
	@DontOverride
	@Deprecated
	public default IBefriendedMob asBefriended()
	{
		return getBefriended();
	}
	
	@DontOverride
	public default IBefriendedMob getBefriended()
	{
		if (this instanceof IBefriendedMob bm)
			return bm;
		else throw new UnsupportedOperationException("IBefriendedSunSensitiveMob: mob missing IBefriendedMob interface.");
	}
	
	@DontOverride
	public default Mob getMob()
	{
		if (this instanceof Mob m)
			return m;
		else throw new UnsupportedOperationException("IBefriendedSunSensitiveMob: wrong object type, must be a mob.");
	}
	
	
	/**
	 * @deprecated Use {@code getSunImmunity} instead
	 * Get checking rules for whether the mob is immune to sun.
	 * When the mob fulfills all rules in {@code sunImmuneNecessaryConditions} and
	 * any one rule in {@code sunImmuneConditions}, it will be sun-immune.
	 * The key is a string to identify each rule (predicate).
	 * If empty, it will be always false (not immune).
	 */
	@DontOverride
	@Deprecated
	public default HashMap<String, Supplier<Boolean>> sunImmuneConditions()
	{
		return this.asBefriended().getTempData().values().sunImmuneConditions;
	}
	
	/**
	 * @deprecated Use {@code getSunImmunity} instead
	 * Get necessary checking rules for whether the mob is immune to sun.
	 * When the mob fulfills all rules in {@code sunImmuneNecessaryConditions} and
	 * any one rule in {@code sunImmuneConditions}, it will be sun-immune.
	 * The key is a string to identify each rule (predicate).
	 * If empty, necessary condition check will be skipped and whether immune to sun only depends on {@code sunImmuneConditions}.
	 */
	@DontOverride
	@Deprecated
	public default HashMap<String, Supplier<Boolean>> sunImmuneNecessaryConditions()
	{
		return this.asBefriended().getTempData().values().sunImmuneNecessaryConditions;
	}
	
	/**
	 * Check if the mob is immune to sun from rules.
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
	public default MutablePredicate<IBefriendedSunSensitiveMob> getSunImmunity()
	{
		return this.getBefriended().getTempData().values().sunImmunity;
	}
	
}
