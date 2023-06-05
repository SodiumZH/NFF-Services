package net.sodiumstudio.befriendmobs.entity.ai.goal;

import java.util.HashSet;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;

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
