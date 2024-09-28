package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFLeapAtTargetGoal extends NFFLeapAtGoal
{

	protected LivingEntity target = null;
	
	public NFFLeapAtTargetGoal(INFFTamed mob, float yd, float xzd, float maxAttackDistance, int chance)
	{
		super(mob, yd, xzd, maxAttackDistance, chance);
	}

	@Override
	public boolean checkCanUse()
	{
		this.target = this.mob.asMob().getTarget();
		this.setTargetPos(target);
		return super.checkCanUse();
	}
	
	@Override
	public void onStart()
	{
		
		super.onStart();
		this.leap();
		this.mob.asMob().setAggressive(true);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		this.mob.asMob().setAggressive(false);
	}
}
