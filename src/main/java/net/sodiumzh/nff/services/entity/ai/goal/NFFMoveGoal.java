package net.sodiumzh.nff.services.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Blocks;
import net.sodiumzh.nff.services.entity.ai.goal.preset.INFFPathfindingGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.INFFTamedAmphibious;

import java.util.function.Predicate;

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
	protected double speedModifier = 1.0d;
	public boolean canFly = false;
	public boolean canSwim = false;
	public boolean canWalk = true;
	public boolean canStepOntoLeaves = false;
	protected boolean isFlying = false;
	protected boolean usesNavigation;

	/* additional modules */
	
	public NFFMoveGoal(INFFTamed mob) {
		super(mob);
	}
	
	public NFFMoveGoal(INFFTamed mob, double speedModifier) {
		super(mob);
		this.speedModifier = speedModifier;
		this.usesNavigation = (mob.asMob() instanceof PathfinderMob && this instanceof INFFPathfindingGoal);
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
	* If amphibious, the mob's {@link INFFTamed} implementation must implement {@link INFFTamedAmphibious} interface.
	 */
	public NFFMoveGoal amphibious()
	{
		usesNavigation = true;
		canSwim = true;
		isAmphibious = true;
		if (!(this.getMob() instanceof INFFTamedAmphibious))
			throw new IllegalStateException("NFF Services: Amphibious move goal requires implementation of INFFTamedAmphibious.");
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

	public double getSpeedModifier() {
		return speedModifier;
	}

	/**
	 * Label whether this goal should use navigation for movement. By default,
	 * it's true if this goal is {@link INFFPathfindingGoal} and false otherwise.
	 * <p>Non-pathfinding mobs will never use navigation despite this value, and will
	 * not cause crash.
	 */
	public NFFMoveGoal setSpeedModifier(double speedModifier) {
		this.speedModifier = speedModifier;
		return this;
	}

	/** Only for ground pathfinding, set the mob can step onto leaves. */
	public NFFMoveGoal canStepOntoLeaves()
	{
		canStepOntoLeaves = true;
		return this;
	}

	public boolean shouldUseNavigation() {
		return usesNavigation;
	}

	public NFFMoveGoal setUsesNavigation(boolean usesNavigation) {
		this.usesNavigation = usesNavigation;
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
