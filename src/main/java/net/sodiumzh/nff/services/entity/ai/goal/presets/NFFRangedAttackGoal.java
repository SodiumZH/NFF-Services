package net.sodiumzh.nff.services.entity.ai.goal.presets;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFRangedAttackGoal extends NFFShootProjectileGoal
{

	protected RangedAttackMob rangedAttackMob;
	
	public NFFRangedAttackGoal(INFFTamed mob, double pSpeedModifier, int pAttackInterval,
			float pAttackRadius)
	{
		super(mob, pSpeedModifier, pAttackInterval, pAttackRadius);
		if (mob instanceof RangedAttackMob ram)
			this.rangedAttackMob = ram;
		else throw new UnsupportedOperationException("NFFRangedAttackGoal requires mob to implement RangedAttackMob interface.");
	}

	public NFFRangedAttackGoal(INFFTamed mob, double pSpeedModifier, int pAttackIntervalMin,
			int pAttackIntervalMax, float pAttackRadius)
	{
		super(mob, pSpeedModifier, pAttackIntervalMin, pAttackIntervalMax, pAttackRadius);
		if (mob instanceof RangedAttackMob ram)
			this.rangedAttackMob = ram;
		else throw new UnsupportedOperationException("NFFRangedAttackGoal requires mob to implement RangedAttackMob interface.");
	}

	@Override
	protected void performShooting(LivingEntity target, float velocity) {
		rangedAttackMob.performRangedAttack(target, velocity);
	}

	@Override
	protected LivingEntity updateTarget() {
		return mob.asMob().getTarget();
	}

}
