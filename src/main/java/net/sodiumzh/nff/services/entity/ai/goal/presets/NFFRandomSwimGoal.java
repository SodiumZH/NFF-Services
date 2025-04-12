package net.sodiumzh.nff.services.entity.ai.goal.presets;

import javax.annotation.Nullable;

import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFRandomSwimGoal extends NFFRandomStrollGoal
{
	public NFFRandomSwimGoal(INFFTamed mob, double speedModifier, int interval)
	{
		super(mob, speedModifier, interval);
	}

	@Override
	@Nullable
	protected Vec3 getPosition() 
	{
		return BehaviorUtils.getRandomSwimmablePos(this.getPathfinder(), 10, 7);
	}
}
