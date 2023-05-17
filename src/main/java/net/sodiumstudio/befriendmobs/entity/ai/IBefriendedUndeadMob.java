package net.sodiumstudio.befriendmobs.entity.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.util.annotation.DontOverride;

/**
 * This is an interface handling sun immunity for undead mobs.
 * Put and remove entries in {@code sunImmuneConditions()} and {@sunImmuneNecessaryConditions()} to set rules.
 * Note: applying sun-immunity on mob must be manually implemented on each mob. 
 */
public interface IBefriendedUndeadMob
{
	@Deprecated
	@DontOverride
	public default void setSunSensitive(boolean value)
	{
		if (value)
		{
			sunImmuneNecessaryConditions().remove("LEGACY_set");
			sunImmuneConditions().put("LEGACY_set", m -> true);
		}
		else
		{
			sunImmuneNecessaryConditions().put("LEGACY_set", m -> false);
			sunImmuneConditions().remove("LEGACY_set");
		}
	}
	
	@DontOverride
	public default IBefriendedMob asBefriended()
	{
		if (this instanceof IBefriendedMob bm)
			return bm;
		else throw new UnsupportedOperationException("IBefriendedUndeadMob: mob missing IBefriendedMob interface.");
	}
	
	
	/**
	 * Get checking rules for whether the mob is immune to sun.
	 * When the mob fulfills all rules in {@code sunImmuneNecessaryConditions} and
	 * any one rule in {@code sunImmuneConditions}, it will be sun-immune.
	 * The key is a string to identify each rule (predicate).
	 * If empty, it will be always false (not immune).
	 */
	@DontOverride
	public default HashMap<String, Predicate<IBefriendedMob>> sunImmuneConditions()
	{
		return this.asBefriended().getTempData().values().sunImmuneConditions;
	}
	
	/**
	 * Get necessary checking rules for whether the mob is immune to sun.
	 * When the mob fulfills all rules in {@code sunImmuneNecessaryConditions} and
	 * any one rule in {@code sunImmuneConditions}, it will be sun-immune.
	 * The key is a string to identify each rule (predicate).
	 * If empty, necessary condition check will be skipped and whether immune to sun only depends on {@code sunImmuneConditions}.
	 */
	@DontOverride
	public default HashMap<String, Predicate<IBefriendedMob>> sunImmuneNecessaryConditions()
	{
		return this.asBefriended().getTempData().values().sunImmuneNecessaryConditions;
	}
	
	/**
	 * Check if the mob is immune to sun from rules.
	 */
	@DontOverride
	public default boolean isSunImmune()
	{
		IBefriendedMob bm = this.asBefriended();
		for (String key: sunImmuneNecessaryConditions().keySet())
		{
			if (!sunImmuneNecessaryConditions().get(key).test(bm))
				return false;
		}
		for (String key: sunImmuneConditions().keySet())
		{
			if (sunImmuneConditions().get(key).test(bm))
				return true;
		}
		return false;
	}
	
}
