package net.sodiumstudio.befriendmobs.entity.ai.goal;

import java.util.HashSet;

import javax.annotation.Nullable;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.util.exceptions.UnimplementedException;

public abstract class BefriendedGoal extends Goal {

	// for simplification
	protected static final BefriendedAIState WAIT = BefriendedAIState.WAIT;
	protected static final BefriendedAIState FOLLOW = BefriendedAIState.FOLLOW;
	protected static final BefriendedAIState WANDER = BefriendedAIState.WANDER;
	
	protected IBefriendedMob mob = null;
	private HashSet<BefriendedAIState> allowedStates = new HashSet<BefriendedAIState>();
	private boolean isBlocked = false;
	/**
	 * If true, this goal will require the mob's owner is in the same level to run.
	 */
	protected boolean requireOwnerPresent = true;
	
	@Deprecated
	public BefriendedGoal()
	{
	}
	
	public BefriendedGoal(IBefriendedMob mob)
	{
		this.mob = mob;
	}
	
	public boolean isStateAllowed()
	{
		return allowedStates.contains(mob.getAIState());
	}
	
	public BefriendedGoal allowState(BefriendedAIState state)
	{
		if (!allowedStates.contains(state))
			allowedStates.add(state);
		return this;
	}
	
	public BefriendedGoal excludeState(BefriendedAIState state)
	{
		if (allowedStates.contains(state))
			allowedStates.remove(state);
		return this;
	}
	
	public BefriendedGoal allowAllStates()
	{
		for (BefriendedAIState state : BefriendedAIState.getAllStates())
			allowedStates.add(state);
		return this;
	}
	
	public BefriendedGoal allowAllStatesExceptWait()
	{
		allowAllStates();
		excludeState(WAIT);
		return this;
	}
	
	public void disallowAllStates()
	{
		allowedStates.clear();
	}
	
	public BefriendedGoal block()
	{
		isBlocked = true;
		return this;
	}
	
	public BefriendedGoal unblock()
	{
		isBlocked = false;
		return this;
	}
	
	public boolean isDisabled()
	{
		return mob.getOwner() == null || isBlocked || !allowedStates.contains(mob.getAIState());
	}
	
	public IBefriendedMob getMob()
	{
		return mob;
	}
	
	/**
	 * Get mob as PathfinderMob
	 * @return mob cast to PathfinderMob, or null if the mob isn't a PathfinderMob
	 */
	@Nullable
	public PathfinderMob getPathfinder()
	{
		return mob instanceof PathfinderMob ? (PathfinderMob)mob : null;
	}
	
	/**
	 * Fixed here because some common checks are needed here.
	 * In subclasses, override {@link checkCanUse} instead.
	 */
	@Override
	public final boolean canUse() 
	{
		if (mob == null || requireOwnerPresent && !mob.isOwnerPresent())
			return false;
		BefriendedGoalCheckCanUseEvent event = new BefriendedGoalCheckCanUseEvent(this, BefriendedGoalCheckCanUseEvent.Phase.CAN_USE);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getManualSetValue().isPresent())
			return event.getManualSetValue().get();
		if (isDisabled())
			return false;
		return checkCanUse();
	}
	
	/**
	 * The alternate of {@code canUse} method for befriend goals.
	 * Override this for {@code canUse} check instead in subclasses. 
	 */
	public abstract boolean checkCanUse();
	
	/**
	 * Fixed here because some common checks are needed here.
	 * In subclasses, override {@link checkCanContinueToUse} instead.
	 */
	@Override
	public final boolean canContinueToUse()
	{
		if (mob == null || requireOwnerPresent && !mob.isOwnerPresent())
			return false;
		BefriendedGoalCheckCanUseEvent event = new BefriendedGoalCheckCanUseEvent(this, BefriendedGoalCheckCanUseEvent.Phase.CAN_CONTINUE_TO_USE);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getManualSetValue().isPresent())
			return event.getManualSetValue().get();
		if (isDisabled())
			return false;
		return checkCanContinueToUse();
	}
	
	/**
	 * The alternate of {@code canContinueToUse} method for befriend goals.
	 * Override this for {@code canContinueToUse} check instead in subclasses. 
	 */
	public boolean checkCanContinueToUse()
	{
		return this.checkCanUse();
	}
	
}
