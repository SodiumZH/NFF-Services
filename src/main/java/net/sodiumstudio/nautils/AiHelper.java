package net.sodiumstudio.nautils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * Utilities related to mob AI operation.
 */
public class AiHelper
{
	
	@SuppressWarnings("unchecked")
	public static Class<? extends LivingEntity> getTargetType(NearestAttackableTargetGoal<?> goal)
	{
		return (Class<? extends LivingEntity>) ReflectHelper.forceGet(goal, NearestAttackableTargetGoal.class, "f_26048_");
	}
	
	public static TargetingConditions getTargetConditions(NearestAttackableTargetGoal<?> goal)
	{
		return (TargetingConditions) ReflectHelper.forceGet(goal, NearestAttackableTargetGoal.class, "f_26051_");
	}
	
	public static boolean isMobHostileTo(Mob test, LivingEntity isHostileTo)
	{
		// Check if the mob has a NearestAttackableTargetGoal<isHostileTo.class> goal
		 for(WrappedGoal goal: test.targetSelector.getAvailableGoals())
		 {
			 if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> natg)
			 {
				 if (getTargetType(natg).isAssignableFrom(isHostileTo.getClass()))
				 {
					 // If found, check if can attack
					 if (getTargetConditions(natg).test(test, isHostileTo))
						 return true;
				 }
			 }
		 }
		 return false;
	}

	/**
	 * Set a mob hostile to an entity type.
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, int priority, Predicate<LivingEntity> condition, boolean noSubclass)
	{
		Predicate<LivingEntity> cond = condition == null ? (l -> true) : condition;
		mob.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<T>(mob, type, true, cond.and((l) -> !noSubclass || l.getClass() == type)));
	}
	
	/**
	 * Set a mob hostile to an entity type. (Auto priority, not recommended)
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 * <p> Not recommended. Specify priority if possible because it doesn't use reflection and is much faster.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, Predicate<LivingEntity> condition, boolean noSubclass)
	{
		if (getTargetPlayerGoal(mob) != null)
		{
			setHostileTo(mob, type, getTargetPlayerGoal(mob).getPriority(), condition, noSubclass);
		}
		else
		{
			setHostileTo(mob, type, 3, condition, noSubclass);
		}
	}
	
	/**
	 * Set a mob hostile to an entity type, allowing subclasses.
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, int priority, Predicate<LivingEntity> condition)
	{
		setHostileTo(mob, type, priority, condition, false);
	}

	/**
	 * Set a mob hostile to an entity type, allowing subclasses.
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, int priority)
	{
		setHostileTo(mob, type, priority, null);
	}
	
	/**
	 * Set a mob hostile to an entity type, including sub type. (Auto priority, not recommended)
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 * <p> Not recommended. Specify priority if possible because it doesn't use reflection and is much faster.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, Predicate<LivingEntity> condition)
	{
		setHostileTo(mob, type, condition, false);
	}
	
	/**
	 * Set a mob hostile to an entity type without additional condition. (Auto priority, not recommended)
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 * <p> Not recommended. Specify priority if possible because it doesn't use reflection and is much faster.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type)
	{
		setHostileTo(mob, type, (l) -> true);
	}
	
	/**
	 * Set a mob not hostile to an entity type.
	 */
	public static <T extends LivingEntity> void setNotHostileTo(Mob mob, Class<T> type)
	{
		for (WrappedGoal goal: mob.targetSelector.getAvailableGoals()) {
			if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> tg)
			{
				if (getTargetType(tg) == type)
					mob.targetSelector.getAvailableGoals().remove(goal);
			}
		}
	}
	
	/**
	 * Get the goal for targeting player of a mob, or null if not having one. 
	 * It returns {@link WrappedGoal}, which contains a {@code NearestAttackableTargetGoal<Player>} or {@code NearestAttackableTargetGoal<ServerPlayer>.} 
	 */
	public static WrappedGoal getTargetPlayerGoal(Mob mob)
	{
		for (WrappedGoal goal: mob.targetSelector.getAvailableGoals()) {
			if(goal.getGoal() instanceof NearestAttackableTargetGoal<?> tg)
			{
				if (Player.class.isAssignableFrom(getTargetType(tg)))
				{
					return goal;
				}				
			}
		}
		return null;
	}
	
	/**
	 * Add a targeting condition to a hostile target goal.
	 * The target will be required to fulfill BOTH the old and new conditions.
	 */
	@SuppressWarnings("unchecked")
	public static void addAndTargetingCondition(NearestAttackableTargetGoal<?> goal, Predicate<LivingEntity> condition)
	{
		TargetingConditions goalCond = getTargetConditions(goal);	// targetConditions
		Predicate<LivingEntity> oldCond = 
				ObfuscationReflectionHelper.getPrivateValue(TargetingConditions.class, goalCond, "f_26879_");	// "selector"
		if (oldCond != null)
			ObfuscationReflectionHelper.setPrivateValue(TargetingConditions.class, goalCond, oldCond.and(condition), "f_26879_");
		else
			ObfuscationReflectionHelper.setPrivateValue(TargetingConditions.class, goalCond, condition, "f_26879_");
	}
	
	/**
	 * Add a targeting condition to a hostile target goal.
	 * The target will be required to fulfill EITHER the old or new condition.
	 * Warning: if there isn't a previously existing check, it will not work because the old condition is always true.
	 */
	@SuppressWarnings("unchecked")
	public static void addOrTargetingCondition(NearestAttackableTargetGoal<?> goal, Predicate<LivingEntity> condition)
	{
		TargetingConditions goalCond = getTargetConditions(goal);	// targetConditions
		Predicate<LivingEntity> oldCond = 
				ObfuscationReflectionHelper.getPrivateValue(TargetingConditions.class, goalCond, "f_26879_");	// "selector"
		if (oldCond != null)
			ObfuscationReflectionHelper.setPrivateValue(TargetingConditions.class, goalCond, oldCond.or(condition), "f_26879_");
	}
	
	/**
	 * Insert a goal at given priority, and postpone all goals at the same or lower priority. 
	 * <p> Note: this is for {@code goalSelector}. For {@code targetSelector}, use {@link AiHelper#insertTargetGoal}.
	 * <p> Note: this method is more costly than {@link GoalSelector#addGoal}. 
	 * If you're sure there's no colliding priority, use {@code mob.goalSelector.addGoal()} instead.
	 */
	public static void insertGoal(Mob mob, Goal goal, int priority)
	{
		boolean needsPostpone = false;
		HashSet<WrappedGoal> toPostpone = new HashSet<WrappedGoal>();
		for (WrappedGoal wg: mob.goalSelector.getAvailableGoals())
		{
			if (wg.getPriority() == priority)
			{
				needsPostpone = true;
			}
			if (wg.getPriority() >= priority)
			{
				toPostpone.add(wg);
			}
		}
		mob.goalSelector.addGoal(priority, goal);
		if (!needsPostpone)
			return;
		for (WrappedGoal wg: toPostpone)
		{
			int oldPriority = wg.getPriority();
			mob.goalSelector.removeGoal(wg.getGoal());
			mob.goalSelector.addGoal(oldPriority + 1, wg.getGoal());
		}
	}
	
	/**
	 * Insert a target goal at given priority, and if needed, postpone all goals at the same or lower priority. 
	 * <p> Note: this is for {@code targetSelector}. For {@code goalSelector}, use {@link AiHelper#insertGoal}.
	 * <p> Note: this method is more costly than {@link GoalSelector#addGoal}. 
	 * If you're sure there's no colliding priority, use {@code mob.targetSelector.addGoal()} instead.
	 */
	public static void insertTargetGoal(Mob mob, TargetGoal goal, int priority)
	{
		boolean needsPostpone = false;
		HashSet<WrappedGoal> toPostpone = new HashSet<WrappedGoal>();
		for (WrappedGoal wg: mob.targetSelector.getAvailableGoals())
		{
			if (wg.getPriority() == priority)
			{
				needsPostpone = true;
			}
			if (wg.getPriority() >= priority)
			{
				toPostpone.add(wg);
			}
		}
		mob.targetSelector.addGoal(priority, goal);
		if (!needsPostpone)
			return;
		for (WrappedGoal wg: toPostpone)
		{
			int oldPriority = wg.getPriority();
			mob.targetSelector.removeGoal(wg.getGoal());
			mob.targetSelector.addGoal(oldPriority + 1, wg.getGoal());
		}
	}
	
	public static HashMap<Goal, Integer> getGoalsAndPriorities(Mob mob)
	{
		HashMap<Goal, Integer> map = new HashMap<Goal, Integer>();
		for (WrappedGoal wg: mob.goalSelector.getAvailableGoals())
		{
			map.put(wg.getGoal(), wg.getPriority());
		}
		return map;
	}
	
	public static HashMap<Goal, Integer> getTargetGoalsAndPriorities(Mob mob)
	{
		HashMap<Goal, Integer> map = new HashMap<Goal, Integer>();
		for (WrappedGoal wg: mob.targetSelector.getAvailableGoals())
		{
			map.put(wg.getGoal(), wg.getPriority());
		}
		return map;
	}
}
