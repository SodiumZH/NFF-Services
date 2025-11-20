package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.util.NFULevelStatics;

import java.util.EnumSet;


// Ported from HMaG
public class NFFFlyingRandomMoveGoal extends NFFFlyingMoveGoal
{
	// Determines how frequently it runs. Higher value means lower frequency. 
	protected final int chance;
	protected final int width;
	protected final int height;
	protected int heightLimit = -1;
	
	public NFFFlyingRandomMoveGoal(INFFTamed mob)
	{
		this(mob, 1.0D);
	}

	public NFFFlyingRandomMoveGoal(INFFTamed mob, double speedModifier)
	{
		this(mob, speedModifier, 6);
	}

	public NFFFlyingRandomMoveGoal(INFFTamed mob, double speedModifier, int chance)
	{
		this(mob, speedModifier, chance, 3, 2);
	}
	
	public NFFFlyingRandomMoveGoal(INFFTamed mob, double speedModifier, int chance, int width, int height)
	{
		super(mob, speedModifier);
		this.chance = chance;
		this.width = width;
		this.height = height;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		//this.allowAllStatesExceptWait();
		this.allowState(NFFTamedMobAIState.WANDER);
	}

	public NFFFlyingRandomMoveGoal heightLimit(int value)
	{
		heightLimit = value;
		return this;
	}
	
	@Override
	public boolean checkCanUse()
	{
		if (isDisabled())
			return false;		
		return !mob.asMob().getMoveControl().hasWanted() && mob.asMob().getRandom().nextInt(this.chance) == 0;
	}

	@Override
	public boolean checkCanContinueToUse()
	{
		return false;
	}

	@Override
	public void onStart()
	{
		/*if (!(mob.asMob().getTarget() != null && mob.asMob().getTarget().isAlive()))
		{
			mob.asMob().setAttackPhase(0);
		}*/
	}

	
	protected BlockPos getWantedPosition()
	{
		BlockPos blockpos = mob.asMob().blockPosition();
		BlockPos blockpos1 = blockpos.offset(
				mob.asMob().getRandom().nextInt(this.width * 2 + 1) - this.width,
				mob.asMob().getRandom().nextInt(this.height * 2 + 1) - this.height,
				mob.asMob().getRandom().nextInt(this.width * 2 + 1) - this.width);
		if (heightLimit <= 0)
			return blockpos1;
		// No height limit if it's above the void
		else if (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) == -1)
			return blockpos1;
		else if (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) > heightLimit)
		{
			// If it's already too high, fly to the height limit first
			int it = 32;
			while (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) > heightLimit)
			{
				blockpos1 = blockpos1.below();
				it--;
				if (it <= 0)
					break;
			}
			// Case when it didn't find a position, try flying down
			if (it <= 0)
			{
				blockpos1 = new BlockPos(blockpos);
				while (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) > heightLimit)
					blockpos1 = blockpos1.below();
			}
			return blockpos1;
		}
		else
		{
			int it = 32;	// To avoid potential infinite loop 				
			while (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) > heightLimit)
			{
				// Search until an acceptable
				blockpos1 = blockpos.offset(
						mob.asMob().getRandom().nextInt(this.width * 2 + 1) - this.width,
						mob.asMob().getRandom().nextInt(this.height * 2 + 1) - this.height,
						mob.asMob().getRandom().nextInt(this.width * 2 + 1) - this.width);
				it--;
				if (it <= 0)
					break;
			}
			// If failed, find below to get an acceptable position
			while (NFULevelStatics.getHeightToGround(blockpos1, mob.asMob()) > heightLimit)
				blockpos1 = blockpos1.below();
			return blockpos1;
		}

	}
	
	@Override
	public void onTick()
	{
		for (int i = 0; i < 6; ++i)
		{
			BlockPos blockpos1 = getWantedPosition();
			
			if (shouldAvoidSun.test(mob) && NFULevelStatics.isUnderSun(blockpos1, mob.asMob()))
				continue;
			
			if (mob.asMob().level.isEmptyBlock(blockpos1))
			{
				this.flyTo(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, this.getSpeedModifier());

				if (mob.asMob().getTarget() == null)
				{
					mob.asMob().getLookControl().setLookAt(blockpos1.getX() + 0.5D, blockpos1.getY() + 0.5D, blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
				}

				break;
			}
		}
	}	
}
