package net.sodiumzh.nff.services.entity.ai.goal.presets;

import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFLeapAtOwnerGoal extends NFFLeapAtGoal implements INFFFollowOwner
{

	public NFFLeapAtOwnerGoal(INFFTamed mob, float yd, float xzd, float maxAttackDistance, int chance)
	{
		super(mob, yd, xzd, maxAttackDistance, chance);
		this.allowState(NFFTamedMobAIState.FOLLOW);
	}

	public NFFLeapAtOwnerGoal(INFFTamed mob, float yd, float xzd)
	{
		super(mob, yd, xzd);
		this.allowState(NFFTamedMobAIState.FOLLOW);
	}

	public NFFLeapAtOwnerGoal(INFFTamed mob, float yd)
	{
		super(mob, yd);
		this.allowState(NFFTamedMobAIState.FOLLOW);
	}

	@Override
	protected boolean canLeap()
	{
		if (targetPos.isEmpty())
			return false;
		return this.mob.asMob().distanceToSqr(this.targetPos.get()) > 4d;
	}
	
	@Override
	public boolean checkCanUse()
	{
		if (!mob.isOwnerPresent())
		{
			this.removeTargetPos();;
			return false;
		}
		this.setTargetPos(mob.getOwner());
		return super.checkCanUse();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		teleportToOwner();
		leap();
	}
}
