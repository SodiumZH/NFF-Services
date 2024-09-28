package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.sodiumzh.nff.services.entity.ai.goal.NFFGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

/* Block the actions below under certain conditions */
public abstract class NFFBlockActionGoal extends NFFGoal
{
	
	public NFFBlockActionGoal(INFFTamed mob)
	{
		super(mob);
	}
	
	@Override
	public boolean checkCanUse()
	{
		return blockCondition();
	}
	
	@Override
	public boolean checkCanContinueToUse()
	{
		return blockCondition();
	}
	
	@Override
	public void onStart()
	{
		mob.asMob().getNavigation().stop();
	}
	
	public abstract boolean blockCondition();
}

