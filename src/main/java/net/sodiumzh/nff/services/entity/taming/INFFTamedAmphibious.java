package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;

public interface INFFTamedAmphibious
{
	public WaterBoundPathNavigation getWaterNav();
	public GroundPathNavigation getGroundNav();
	public PathNavigation getAppliedNav();
	public void switchNav(boolean isWaterNav);
	
	public default INFFTamed asTamed()
	{
		return (INFFTamed)this;
	}
	public default PathfinderMob asPathfinder()
	{
		return (PathfinderMob)this;
	}
}
