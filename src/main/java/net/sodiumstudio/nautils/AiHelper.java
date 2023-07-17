package net.sodiumstudio.nautils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.befriendmobs.BefriendMobs;

/**
 * Utilities related to mob AI operation.
 */
public class AiHelper
{
	/**
	 * Get the priority of NearestAttackTargetGoal<Player> of a hostile mob.
	 * @return the priority, or -1 if not having one.
	 */
	public static int getAttackPlayerGoalPriority(Mob mob)
	{
		for (WrappedGoal goal: mob.targetSelector.getAvailableGoals()) {
			if(goal.getGoal() instanceof NearestAttackableTargetGoal<?> tg)
			{
				Class<?> type = (Class<?>) ReflectHelper.forceGet(tg, NearestAttackableTargetGoal.class, "targetType", true);
				if (type == Player.class || type == ServerPlayer.class)
					return goal.getPriority();
			}
		}
		return -1;
	}
	
	public static boolean isMobHostileTo(Mob test, LivingEntity isHostileTo)
	{
		// Check if the mob has a NearestAttackableTargetGoal<isHostileTo.class> goal
		 for(WrappedGoal goal: test.targetSelector.getAvailableGoals())
		 {
			 if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> natg)
			 {
				 Class<?> targetType = (Class<?>) ReflectHelper.forceGet(natg, NearestAttackableTargetGoal.class, "targetConditions", true);
				 if (targetType == isHostileTo.getClass())
				 {
					 return true;
				 }
			 }
		 }
		 return false;
	}
	
	/** @deprecated Something is wrong with this function, it works correctly in IDE but not in game */
	@Deprecated
	public static boolean isMobHostileToPlayer(Mob test)
	{
		// Check if the mob has a NearestAttackableTargetGoal<Player> goal
		 for (WrappedGoal goal: test.targetSelector.getAvailableGoals())
		 {
			 if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> natg)
			 {
				 // Force get a private field by reflection
				 Class<?> targetType = (Class<?>) ReflectHelper.forceGet(natg, NearestAttackableTargetGoal.class, "targetType", true);
				 if (targetType == Player.class || targetType == ServerPlayer.class)
				 {
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
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, Predicate<LivingEntity> condition, boolean noSubclass)
	{
		if (getAttackPlayerGoalPriority(mob) >= 0)
		{
			mob.targetSelector.addGoal(getAttackPlayerGoalPriority(mob), new NearestAttackableTargetGoal<T>(mob, type, true, condition.and((l) -> !noSubclass || l.getClass() == type)));
		}
		else
			mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<T>(mob, type, true, condition.and((l) -> !noSubclass || l.getClass() == type)));
	}
	
	/**
	 * Set a mob hostile to an entity type, including sub type.
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
	 */
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, Predicate<LivingEntity> condition)
	{
		setHostileTo(mob, type, condition, false);
	}
	
	/**
	 * Set a mob hostile to an entity type without additional condition.
	 * The target goal priority will be the same as the mob targeting player. If the mob isn't hostile to player, the priority will be 3.
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
			if(goal.getGoal() instanceof NearestAttackableTargetGoal<?> tg)
			{
				Class<?> goalType = (Class<?>) ReflectHelper.forceGet(tg, NearestAttackableTargetGoal.class, "targetType", true);
				if (goalType == type)
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
				Class<?> goalType = (Class<?>) ReflectHelper.forceGet(tg, NearestAttackableTargetGoal.class, "targetType", true);
				if (goalType == Player.class || goalType == ServerPlayer.class)
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
		TargetingConditions goalCond = (TargetingConditions) ReflectHelper.forceGet(goal, NearestAttackableTargetGoal.class, "targetConditions");
		if (goalCond == null)
		{
			Mob mob = (Mob)ReflectHelper.forceGet(goal, TargetGoal.class, "mob", true);
			NaUtils.LOGGER.error("AiHelper#addAndTargetingCondition: failed to load target conditions. Mob: " + (mob != null ? mob.getName().getString() : "(UNKNOWN)"));
			return;
		}	
		Predicate<LivingEntity> oldCond = (Predicate<LivingEntity>) ReflectHelper.forceGet(goalCond, TargetingConditions.class, "selector");
		if (oldCond != null)
			ReflectHelper.forceSet(goalCond, TargetingConditions.class, "selector", oldCond.and(condition));
		else
			ReflectHelper.forceSet(goalCond, TargetingConditions.class, "selector", condition);
	}
	
	/**
	 * Add a targeting condition to a hostile target goal.
	 * The target will be required to fulfill EITHER the old or new condition.
	 * Warning: if there isn't a previously existing check, it will not work because the old condition is always true.
	 */
	@SuppressWarnings("unchecked")
	public static void addOrTargetingCondition(NearestAttackableTargetGoal<?> goal, Predicate<LivingEntity> condition)
	{
		if (goal == null) return;
		TargetingConditions goalCond = (TargetingConditions) ReflectHelper.forceGet(goal, NearestAttackableTargetGoal.class, "targetConditions");
		if (goalCond == null)
		{
			Mob mob = (Mob)ReflectHelper.forceGet(goal, TargetGoal.class, "mob", true);
			NaUtils.LOGGER.error("AiHelper#addOrTargetingCondition: failed to load target conditions. Mob: " + (mob != null ? mob.getName().getString() : "(UNKNOWN)"));
			return;
		}	
		Predicate<LivingEntity> oldCond = (Predicate<LivingEntity>) ReflectHelper.forceGet(goalCond, TargetingConditions.class, "selector");
		if (oldCond != null)
			ReflectHelper.forceSet(goalCond, TargetingConditions.class, "selector", oldCond.or(condition));
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
