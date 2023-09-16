package net.sodiumstudio.befriendmobs.entity.ai.goal;

import java.util.HashSet;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.bmevents.entity.ai.BefriendedGoalCheckCanUseEvent;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;

public abstract class BefriendedTargetGoal extends TargetGoal implements IBefriendedGoal
{

	// for simplification
	protected static final BefriendedAIState WAIT = BefriendedAIState.WAIT;
	protected static final BefriendedAIState FOLLOW = BefriendedAIState.FOLLOW;
	protected static final BefriendedAIState WANDER = BefriendedAIState.WANDER;

	protected IBefriendedMob mob = null;
	protected HashSet<BefriendedAIState> allowedStates = new HashSet<BefriendedAIState>();
	protected boolean isBlocked = false;
	protected Random rnd = new Random();
	/**
	 *  If it's more than 0, when checking if this goal should be executed (only on start, not on check continue using),
	 *  it will has chance to be directly skipped, allowing goals below to be executed.
	 */
	protected double skipChance = 0;
	/**
	 * If true, this goal will require the mob's owner is in the same level to run.
	 */
	protected boolean requireOwnerPresent = true;
	/**
	 * Additional condition to start this goal. (Not checked on checking continue to use)
	 */
	protected Predicate<BefriendedTargetGoal> startCondition = null;
	
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
	
	@Override
	public boolean isStateAllowed() {
		return allowedStates.contains(mob.getAIState());
	}

	@Override
	public IBefriendedGoal allowState(BefriendedAIState state) {
		if (!allowedStates.contains(state))
			allowedStates.add(state);
		return this;
	}

	@Override
	public IBefriendedGoal excludeState(BefriendedAIState state) {
		if (allowedStates.contains(state))
			allowedStates.remove(state);
		return this;
	}

	@Override
	public IBefriendedGoal allowAllStates() {
		for (BefriendedAIState state : BefriendedAIState.getAllStates())
			allowedStates.add(state);
		return this;
	}

	@Override
	public IBefriendedGoal allowAllStatesExceptWait() {
		allowAllStates();
		excludeState(WAIT);
		return this;
	}

	@Override
	public void disallowAllStates() {
		allowedStates.clear();
	}

	@Override
	public IBefriendedGoal block() {
		isBlocked = true;
		return this;
	}

	@Override
	public IBefriendedGoal unblock() {
		isBlocked = false;
		return this;
	}

	@Override
	public boolean isDisabled() {
		return isBlocked || !isStateAllowed();
	}

	public LivingEntity getLiving() {
		return (LivingEntity) mob;
	}

	@Override
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
	 * In subclasses, override {@link IBefriendedGoal#checkCanUse} instead.
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
		if (startCondition != null && !startCondition.test(this))
			return false;
		if (skipChance > 0 && rnd.nextDouble() < skipChance)
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
	 * Fixed here because some common checks are needed here.
	 * In subclasses, override {@link IBefriendedGoal#checkCanContinueToUse} instead.
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
		return checkCanContinueToUse() && super.canContinueToUse();
	}
	
	@Override
	public boolean checkCanContinueToUse()
	{
		return true;
	}
	
	/**
	 * Get skip chance of this goal.
	 * <p> If it has skip chance > 0, it will have a chance to be directly skipped when checking if to start, allowing goals below to be executed.
	 * When it's skipped, it won't post {@link BefriendedGoalCheckCanUseEvent}.
	 */
	public double getSkipChance()
	{
		return skipChance;
	}
	
	/**
	 * Set skip chance of this goal. This method returns the goal itself, and use template class to specify the subclass to cast.
	 * <p> If it has skip chance > 0, it will have a chance to be directly skipped when checking if to start, allowing goals below to be executed.
	 * When it's skipped, it won't post {@link BefriendedGoalCheckCanUseEvent}.
	 */
	@SuppressWarnings("unchecked")
	public <T extends BefriendedTargetGoal> T setSkipChance(double value)
	{
		this.skipChance = value;
		return (T)this;
	}
	
	/**
	 * Set additional start condition of this goal. This method returns the goal itself, and use template class to specify the subclass to cast.
	 */
	@SuppressWarnings("unchecked")
	public <T extends BefriendedTargetGoal> T setStartCondition(Predicate<BefriendedTargetGoal> condition)
	{
		this.startCondition = condition;
		return (T)this;
	}
	
	/**
	 * Set if requires the owner to be present to start this goal.
	 */
	@SuppressWarnings("unchecked")
	public <T extends BefriendedTargetGoal> T setRequireOwnerPresent(boolean value)
	{
		this.requireOwnerPresent = value;
		return (T)this;
	}
	
}
