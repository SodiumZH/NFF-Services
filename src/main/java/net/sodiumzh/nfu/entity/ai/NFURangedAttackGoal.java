package net.sodiumzh.nfu.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.sodiumzh.nfu.util.NFUMathStatics;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Same function as {@link RangedAttackGoal}, but this goal doesn't require mob to have {@link RangedAttackMob} interface.
 * This also can fully replace vanilla {@link RangedAttackGoal} and can correctly invoke {@link RangedAttackMob#performRangedAttack}.
 * <p>Note: this goal doesn't inherit {@link RangedAttackGoal}, and the possible {@code instanceof RangedAttackGoal} checks will not work.
 */
public class NFURangedAttackGoal<T extends Mob> extends Goal {
    private final T mob;
    @Nullable
    private LivingEntity target;
    private int attackTime = -1;
    private final double speedModifier;
    private int seeTime;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;
    private ShootingAction<T> shootingAction;
    private double minAttackDistance = 0;

    public NFURangedAttackGoal(T mob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        this(mob, pSpeedModifier, pAttackInterval, pAttackInterval, pAttackRadius);
    }

    public NFURangedAttackGoal(T mob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius) {
        this.mob = mob;
        this.speedModifier = pSpeedModifier;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = pAttackRadius;
        this.attackRadiusSqr = pAttackRadius * pAttackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public T getMob() {
        return mob;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();

        if (livingentity != null && livingentity.isAlive()
             && NFUMathStatics.getBoxSurfaceDistSqr(this.getMob().getBoundingBox(), livingentity.getBoundingBox()) >= this.minAttackDistance * this.minAttackDistance) {
            this.target = livingentity;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.canUse() || this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!flag) {
                return;
            }

            float f = (float)Math.sqrt(d0) / this.attackRadius;
            float f1 = Mth.clamp(f, 0.1F, 1.0F);
            if (this.shootingAction != null)
                this.shootingAction.shoot(this.mob, this.target, f1);
            if (this.mob instanceof RangedAttackMob r)
                r.performRangedAttack(this.target, f1);
            this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }
    }

    /**
     * Set action on this goal try to perform ranged attack (i.e. shooting). If the mob is a {@link RangedAttackMob},
     * this action will be invoked before {@link RangedAttackMob#performRangedAttack}.
     */
    public NFURangedAttackGoal<T> setShootingAction(@Nullable ShootingAction<T> action) {
        this.shootingAction = action;
        return this;
    }

    public NFURangedAttackGoal<T> minAttackDistance(double val) {
        this.minAttackDistance = val;
        return this;
    }

    @FunctionalInterface
    public static interface ShootingAction<T extends Mob> {
        public void shoot(T shooter, LivingEntity target, float speed);
    }

}
