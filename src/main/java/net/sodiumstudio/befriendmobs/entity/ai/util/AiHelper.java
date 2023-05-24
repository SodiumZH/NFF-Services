package net.sodiumstudio.befriendmobs.entity.ai.util;

import java.util.function.Predicate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.sodiumstudio.befriendmobs.util.ReflectHelper;

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
				Class<?> type = (Class<?>) ReflectHelper.forceGet(tg, NearestAttackableTargetGoal.class, "targetType");
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
				 Class<?> targetType = (Class<?>) ReflectHelper.forceGet(natg, NearestAttackableTargetGoal.class, "targetConditions");
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
				 Class<?> targetType = (Class<?>) ReflectHelper.forceGet(natg, NearestAttackableTargetGoal.class, "targetType");
				 if (targetType == Player.class)
				 {
					 return true;
				 }
			 }
		 }
		 return false;
	}
	
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type, Predicate<LivingEntity> condition)
	{
		if (getAttackPlayerGoalPriority(mob) >= 0)
		{
			mob.targetSelector.addGoal(getAttackPlayerGoalPriority(mob), new NearestAttackableTargetGoal<T>(mob, type, true));
		}
		else
			mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<T>(mob, type, true, condition));
	}
	
	public static <T extends LivingEntity> void setHostileTo(Mob mob, Class<T> type)
	{
		setHostileTo(mob, type, (l) -> true);
	}
	
}
