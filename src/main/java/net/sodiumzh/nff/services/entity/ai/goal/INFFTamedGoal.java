package net.sodiumzh.nff.services.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface INFFTamedGoal
{
	/**
	 * Check if an AI state is allowed for this goal.
	 */
	public boolean isStateAllowed();

	/**
	 * Set this goal is allowed for the given AI state.
	 */
	public INFFTamedGoal allowState(NFFTamedMobAIState state);

	/**
	 * Set this goal is disabled for the given AI state.
	 */
	public INFFTamedGoal excludeState(NFFTamedMobAIState state);

	/**
	 * Set this goal is allowed for all AI states.
	 */
	public INFFTamedGoal allowAllStates();

	/**
	 * Set this goal is allowed for all AI states except {@link NFFTamedMobAIState#WAIT}.
	 */
	public INFFTamedGoal allowAllStatesExceptWait();

	/**
	 * Remove all allowed AI states.
	 */
	public void disallowAllStates();

	/**
	 * Temporarily disable this goal.
	 */
	public INFFTamedGoal block();

	/**
	 * Resume this goal from temporary disabling.
	 */
	public INFFTamedGoal unblock();

	/**
	 * Get the corresponding mob this goal is controlling.
	 */
	public INFFTamed getMob();

	/**
	 * Check if this goal is disabled by any way, either AI state restriction or temporary disabling.
	 */
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
