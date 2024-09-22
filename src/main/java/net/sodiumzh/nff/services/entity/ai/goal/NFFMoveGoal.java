package net.sodiumzh.nff.services.entity.ai.goal;

import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.INFFTamedAmphibious;

/**
 * The base class of all befriended mob goals for moving to somewhere,
 * including pathfinding for PathfinderMob, flying for FlyMob, etc.
 */
public abstract class NFFMoveGoal extends NFFGoal
{
	/** True if the mob is a PathfinderMob. */
	public boolean isPathfinding = true;
	/** Check condition that the mob should avoid sun. */
	public Predicate<INFFTamed> shouldAvoidSun = (mob -> false);
	/** Only for Pathfinder. If true, the mob should have both ground and water navigation, and must implement {@link INFFTamedAmphibious} interface. */
	public boolean isAmphibious = false;
	public double speedModifier = 1.0d;
	public boolean canFly = false;
	public boolean canSwim = false;
	public boolean canWalk = true;
	public boolean canStepOntoLeaves = false;
	protected boolean isFlying = false;
	
	/* additional modules */
	
	public NFFMoveGoal(INFFTamed mob) {
		super(mob);
	}
	
	public NFFMoveGoal(INFFTamed mob, double speedModifier) {
		super(mob);
		this.speedModifier = speedModifier;
	}
	
	/* Additional modules */
	
	/** Set the goal should always avoid sun */
	public NFFMoveGoal alwaysAvoidSun()
	{
		shouldAvoidSun = (mob -> true);
		return this;
	}
	
	/** Set the sun-avoiding condition of the mob */
	public NFFMoveGoal avoidSunCondition(Predicate<INFFTamed> condition)
	{
		shouldAvoidSun = condition;
		return this;
	}
	
	/** (Must be pathfinding)
	 * <p>Set this goal should support amphibious mobs (having both water and ground navigations).
	* If amphibious, the mob must implement INFFTamedAmphibious interface.
	 */
	public NFFMoveGoal amphibious()
	{
		canSwim = true;
		isAmphibious = true;
		return this;
	}
	
	/** (Must be pathfinding)
	 * <p>Set the mob can only swim in water (or other liquid) and cannot move outside water.
	 */
	public NFFMoveGoal waterOnly()
	{
		canWalk = false;
		canSwim = true;
		return this;
	}
	
	public NFFMoveGoal canFly()
	{
		canFly = true;
		return this;
	}
	
	/**
	 * Set the mob can only fly. Either pathfinding or not.
	 */
	public NFFMoveGoal flyOnly()
	{
		canWalk = false;
		canSwim = false;
		canFly = true;
		return this;
	}
	
	/** Only for ground pathfinding, set the mob can step onto leaves. */
	public NFFMoveGoal canStepOntoLeaves()
	{
		canStepOntoLeaves = true;
		return this;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (isAmphibious)
		{
			if (mob.asMob().isInWater() && mob.asMob().level.getBlockState(new BlockPos(mob.asMob().getEyePosition())).is(Blocks.WATER))
				((INFFTamedAmphibious)mob).switchNav(true);
			else ((INFFTamedAmphibious)mob).switchNav(false);		
		}
	}
	
}
