package net.sodiumstudio.befriendmobs.util;

import java.util.function.Predicate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

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
	
	public static boolean isMobHostileToPlayer(Mob test)
	{
		// Check if the mob has a NearestAttackableTargetGoal<Player> goal
		 for (WrappedGoal goal: test.targetSelector.getAvailableGoals())
		 {
			 if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> natg)
			 {
				 Class<?> targetType = (Class<?>) ReflectHelper.forceGet(natg, NearestAttackableTargetGoal.class, "targetType", true);
				 if (targetType == Player.class)
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
	
}
