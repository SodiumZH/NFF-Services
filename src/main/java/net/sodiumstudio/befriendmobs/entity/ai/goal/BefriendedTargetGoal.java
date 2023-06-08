package net.sodiumstudio.befriendmobs.entity.ai.goal;

import java.util.HashSet;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.util.ReflectHelper;

public abstract class BefriendedTargetGoal extends TargetGoal
{

	// for simplification
	protected static final BefriendedAIState WAIT = BefriendedAIState.WAIT;
	protected static final BefriendedAIState FOLLOW = BefriendedAIState.FOLLOW;
	protected static final BefriendedAIState WANDER = BefriendedAIState.WANDER;

	protected IBefriendedMob mob = null;
	protected HashSet<BefriendedAIState> allowedStates = new HashSet<BefriendedAIState>();
	protected boolean isBlocked = false;
	/**
	 * If true, this goal will require the mob's owner is in the same level to run.
	 */
	protected boolean requireOwnerPresent = true;
	
	public BefriendedTargetGoal(IBefriendedMob mob, boolean mustSee)
	{
		this(mob, mustSee, false);
	}

	public BefriendedTargetGoal(IBefriendedMob mob, boolean mustSee, boolean mustReach)
	{
		super(mob.asMob(), mustSee, mustReach);
		this.mob = mob;
	}

	public HashSet<BefriendedAIState> getAllowedStates()
	{
		return allowedStates;
	}
	
	public boolean isStateAllowed() {
		return allowedStates.contains(mob.getAIState());
	}

	public BefriendedTargetGoal allowState(BefriendedAIState state) {
		if (!allowedStates.contains(state))
			allowedStates.add(state);
		return this;
	}

	public BefriendedTargetGoal excludeState(BefriendedAIState state) {
		if (allowedStates.contains(state))
			allowedStates.remove(state);
		return this;
	}

	public BefriendedTargetGoal allowAllStates() {
		for (BefriendedAIState state : BefriendedAIState.getAllStates())
			allowedStates.add(state);
		return this;
	}

	public BefriendedTargetGoal allowAllStatesExceptWait() {
		allowAllStates();
		excludeState(WAIT);
		return this;
	}

	// Disable this goal
	public void blockGoal() {
		isBlocked = true;
	}

	public void resumeGoal() {
		isBlocked = false;
	}

	public boolean isDisabled() {
		return isBlocked || !isStateAllowed();
	}

	public LivingEntity getLiving() {
		return (LivingEntity) mob;
	}

	public IBefriendedMob getMob() {
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
		// Detect if checkCanUse() calling canUse() which leads to infinite loop
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length > 2 && stacktrace[2].getMethodName().equals("checkCanUse"))
			throw new RuntimeException("Illegal method call: checkCanUse() method cannot call canUse() method inside, otherwise an infinite loop will occur. To get super class' check, call checkCanUse().");
		if (mob == null || requireOwnerPresent && !mob.isOwnerPresent())
			return false;
		BefriendedGoalCheckCanUseEvent event = new BefriendedGoalCheckCanUseEvent(this, BefriendedGoalCheckCanUseEvent.Phase.CAN_USE);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getManualSetValue().isPresent())
			return event.getManualSetValue().get();
		if (isDisabled())
			return false;
		if (isTooFar())
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
		// Detect if checkCanContinueToUse() calling canContinueToUse() which leads to infinite loop
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length > 2 && stacktrace[2].getMethodName().equals("checkCanContinueToUse"))
			throw new RuntimeException("Illegal method call: checkCanContinueToUse() method cannot call canContinueToUse() method inside, otherwise an infinite loop will occur. To get super class' check, call checkCanContinueToUse().");
		if (mob == null || requireOwnerPresent && !mob.isOwnerPresent())
			return false;
		BefriendedGoalCheckCanUseEvent event = new BefriendedGoalCheckCanUseEvent(this, BefriendedGoalCheckCanUseEvent.Phase.CAN_CONTINUE_TO_USE);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getManualSetValue().isPresent())
			return event.getManualSetValue().get();
		if (isDisabled())
			return false;
		if (isTooFar())
		{
			removeTarget();
			return false;
		}
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

	/** Return if the mob hasn't seen the target for set time. */
	public boolean isUnseenTimeout()
	{
		if (!this.mustSee)
			return false;
		else return (Integer)ReflectHelper.forceGet(this, TargetGoal.class, "unseenTicks") > reducedTickDelay(this.unseenMemoryTicks);
	}
	
	/**
	 * Return if the mob is too far from its target.
	 * @return If it's too far, or false if target is invalid.
	 */
	public boolean isTooFar()
	{
		if (mob.asMob().getTarget() == null || !mob.asMob().getTarget().isAlive())
			return false;
		else return this.mob.asMob().distanceToSqr(this.mob.asMob().getTarget()) > this.getFollowDistance() *  this.getFollowDistance();
	}
	
	/** Remove the mob's current target. */
	public void removeTarget()
	{
		LivingEntity previousTarget = this.mob.asMob().getTarget();
		if (previousTarget == null) return;	// As it may execute on tick, filter to prevent calling reflect too many times
		this.mob.asMob().setTarget(null);
		// Remove all TargetGoals' target, since we've made target nullization unnecessary on stop, which may leave some TargetGoals' targetMob param not synced
		for (WrappedGoal goal: this.mob.asMob().targetSelector.getAvailableGoals())
		{
			if (goal.getGoal() instanceof TargetGoal tg)
			{
				if (ReflectHelper.forceGet(tg, TargetGoal.class, "targetMob") == previousTarget)			
					ReflectHelper.forceSet(tg, TargetGoal.class, "targetMob", null);
			}
		}
	}
	
	/**
	 * By default befriended mobs will not remove target immediately on target goal change.
	 * They remove target only when target is invalid, distance is too far or unseen.
	 */
	@Override
	public void stop() {
		if (this.mob.asMob().getTarget() == null // Target is already nullized
			|| !this.mob.asMob().getTarget().isAlive() // Target is invalid
			|| isDisabled() // Blocked by BM mechanism
			|| isTooFar()	// Too far away
			|| isUnseenTimeout() && !this.mob.asMob().hasLineOfSight(this.mob.asMob().getTarget()))	// Unseen
		{
			removeTarget();
		}
	}
}
