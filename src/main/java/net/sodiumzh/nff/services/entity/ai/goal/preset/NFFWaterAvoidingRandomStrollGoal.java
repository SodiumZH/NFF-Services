package net.sodiumzh.nff.services.entity.ai.goal.preset;

import javax.annotation.Nullable;

import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

// Adjusted from vanilla WaterAvoidingRandomStrollGoal
public class NFFWaterAvoidingRandomStrollGoal extends NFFRandomStrollGoal {
	
	public static final float PROBABILITY = 0.001F;
	protected final float probability;

	public NFFWaterAvoidingRandomStrollGoal(INFFTamed pMob, double pSpeedModifier)
	{
		this(pMob, pSpeedModifier, 0.001F);
	}

	public NFFWaterAvoidingRandomStrollGoal(INFFTamed pMob, double pSpeedModifier, float pProbability)
	{
		super(pMob, pSpeedModifier);
		this.probability = pProbability;
	}

	@Override
	@Nullable
	protected Vec3 getPosition() {
		if (getPathfinder().isInWaterOrBubble())
		{
			Vec3 vec3 = LandRandomPos.getPos(getPathfinder(), 15, 7);
			return vec3 == null ? super.getPosition() : vec3;
		} else
		{
			return getPathfinder().getRandom().nextFloat() >= this.probability
					? LandRandomPos.getPos(getPathfinder(), 10, 7)
					: super.getPosition();
		}
	}
	   
}
