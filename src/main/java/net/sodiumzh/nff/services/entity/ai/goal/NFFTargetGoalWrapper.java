package net.sodiumzh.nff.services.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * A wrapper of a generic {@link TargetGoal} enabling features of {@link NFFTargetGoal}.
 */
public class NFFTargetGoalWrapper<T extends TargetGoal> extends NFFTargetGoal {

    private final T goal;

    private NFFTargetGoalWrapper(INFFTamed mob, T goal) {
        super(mob,
                NFUReflectionStatics.forceGet(goal, TargetGoal.class, "f_26136_"/*"mustSee"*/).cast(),
                NFUReflectionStatics.forceGet(goal, TargetGoal.class, "f_26131_"/*"mustReach"*/).cast());
        this.goal = goal;
    }

    public static <T extends TargetGoal> NFFTargetGoalWrapper<T> create(INFFTamed mob, T goal) {
        Mob goalMob = NFUReflectionStatics.forceGet(goal, TargetGoal.class, "f_26135_"/*"mob"*/).cast();
        if (!INFFTamed.isTamedAnd(goalMob, m -> Objects.equals(m, mob)))
            throw new IllegalArgumentException("NFFTargetGoalWrapper#create: input goal's mob isn't equal to " +
                    "the input tamed mob.");
        return new NFFTargetGoalWrapper<>(mob, goal);
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
        return "NFFTargetGoalWrapper{" + this.getGoal().toString() + "}";
    }

    public EnumSet<Goal.Flag> getFlags() {
        return this.getGoal().getFlags();
    }

    protected int adjustedTickDelay(int pAdjustment) {
        return NFUReflectionStatics.forceInvokeRetVal(this.getGoal(), Goal.class, "m_183277_", // adjustedTickDelay
                int.class, pAdjustment).cast();
    }

    protected double getFollowDistance() {
        return NFUReflectionStatics.forceInvokeRetVal(
                this.getGoal(), TargetGoal.class, "m_7623_").cast();
    }


    protected boolean canAttack(@Nullable LivingEntity pPotentialTarget, TargetingConditions pTargetPredicate) {
        return NFUReflectionStatics.forceInvokeRetVal(
                this.getGoal(), TargetGoal.class, "m_26150_",
                LivingEntity.class, pPotentialTarget, TargetingConditions.class, pTargetPredicate).cast();
    }

    public TargetGoal setUnseenMemoryTicks(int pUnseenMemoryTicks) {
        this.getGoal().setUnseenMemoryTicks(pUnseenMemoryTicks);
        return this;
    }

}
