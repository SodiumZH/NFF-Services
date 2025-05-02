package net.sodiumzh.nff.services.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.sodiumzh.nfu.util.NFUReflectionStatics;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

import java.util.EnumSet;

/**
 * A wrapper of a generic {@link Goal} enabling features of {@link NFFGoal}.
 */
public class NFFGoalWrapper<T extends Goal> extends NFFGoal {

    private final T goal;

    private NFFGoalWrapper(INFFTamed mob, T goal) {
        super(mob);
        if (goal instanceof TargetGoal)
            throw new IllegalArgumentException("NFFGoalWrapper: not supporting TargetGoal. Use NFFTargetGoalWrapper instead.");
        this.goal = goal;
    }

    public static <T extends Goal> NFFGoalWrapper<T> create(INFFTamed mob, T goal)
    {
        return new NFFGoalWrapper<>(mob, goal);
    }

    public T getGoal() {
        return goal;
    }

    @Override
    public boolean checkCanUse() {
        return this.getGoal().canUse();
    }

    @Override
    public boolean checkCanContinueToUse() {
        return this.getGoal().canContinueToUse();
    }

    @Override
    public void onStart() {
        this.getGoal().start();
    }

    @Override
    public void onStop() {
        this.getGoal().stop();
    }

    @Override
    public void onTick() {
        this.getGoal().tick();
    }

    public boolean isInterruptable() {
        return this.getGoal().isInterruptable();
    }

    public boolean requiresUpdateEveryTick() {
        return this.getGoal().requiresUpdateEveryTick();
    }

    public void setFlags(EnumSet<Flag> pFlagSet) {
        this.getGoal().setFlags(pFlagSet);
    }

    public String toString() {
        return "NFFGoalWrapper{" + this.getGoal().toString() + "}";
    }

    public EnumSet<Goal.Flag> getFlags() {
        return this.getGoal().getFlags();
    }

    protected int adjustedTickDelay(int pAdjustment) {
        return NFUReflectionStatics.forceInvokeRetVal(this.getGoal(), Goal.class, "m_183277_", // adjustedTickDelay
                int.class, pAdjustment).cast();
    }
}
