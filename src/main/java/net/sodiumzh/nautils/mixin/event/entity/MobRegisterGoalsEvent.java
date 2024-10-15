package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.sodiumzh.nautils.events.NaUtilsLivingEvent;

/**
 * Posted right after mob registering goals.
 */
public class MobRegisterGoalsEvent extends NaUtilsLivingEvent<Mob> {

    private final GoalSelector goalSelector;
    private final GoalSelector targetSelector;

    public MobRegisterGoalsEvent(Mob entity) {
        super(entity);
        this.goalSelector = entity.goalSelector;
        this.targetSelector = entity.targetSelector;
    }

    public GoalSelector getGoalSelector() {
        return goalSelector;
    }

    public GoalSelector getTargetSelector() {
        return targetSelector;
    }

    public MobRegisterGoalsEvent registerGoal(int priority, Goal goal)
    {
        this.goalSelector.addGoal(priority, goal);
        return this;
    }

    public MobRegisterGoalsEvent registerTargetGoal(int priority, TargetGoal goal)
    {
        this.targetSelector.addGoal(priority, goal);
        return this;
    }
}
