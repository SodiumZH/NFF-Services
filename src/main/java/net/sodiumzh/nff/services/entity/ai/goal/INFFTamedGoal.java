package net.sodiumzh.nff.services.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sodiumzh.nautils.annotation.DontOverride;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public interface INFFTamedGoal
{
	public boolean isStateAllowed();
	public INFFTamedGoal allowState(NFFTamedMobAIState state);
	public INFFTamedGoal excludeState(NFFTamedMobAIState state);
	public INFFTamedGoal allowAllStates();
	public INFFTamedGoal allowAllStatesExceptWait();
	public void disallowAllStates();
	public INFFTamedGoal block();	
	public INFFTamedGoal unblock();	
	public INFFTamed getMob();
	public boolean isDisabled();
	/**
	 * @return this cast to Goal.
	 */
	@DontOverride
	public default Goal asGoal()
	{
		return (Goal)this;
	}
	
	/**
	 * The alternate of {@code canUse} method for befriend goals.
	 * Override this for {@code canUse} check instead in subclasses. 
	 */
	public boolean checkCanUse();
		
	/**
	 * The alternate of {@code canContinueToUse} method for befriend goals.
	 * Override this for {@code canContinueToUse} check instead in subclasses. 
	 */
	public default boolean checkCanContinueToUse()
	{
		return this.checkCanUse();
	}
	
	/**
	 * The alternate of {@code start} method for befriend goals.
	 * Override this for {@code start} check instead in subclasses. 
	 */
	public default void onStart()
	{
		return;
	}
	
	/**
	 * The alternate of {@code tick} method for befriend goals.
	 * Override this for {@code tick} check instead in subclasses. 
	 */
	public default void onTick()
	{
		return;
	}
	
	/**
	 * The alternate of {@code stop} method for befriend goals.
	 * Override this for {@code stop} check instead in subclasses. 
	 */
	public default void onStop()
	{
		return;
	}
}
