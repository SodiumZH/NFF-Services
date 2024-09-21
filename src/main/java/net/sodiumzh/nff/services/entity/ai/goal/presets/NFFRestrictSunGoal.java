package net.sodiumzh.nff.services.entity.ai.goal.presets;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.sodiumzh.nff.services.entity.ai.goal.NFFMoveGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.INFFTamedAmphibious;
import net.sodiumzh.nff.services.entity.taming.INFFTamedSunSensitiveMob;

// Adjusted from vanilla RestrictSunGoal
public class NFFRestrictSunGoal extends NFFMoveGoal {

	// If true, the mob will restrict sun although having a helmet.
	public boolean ignoreHelmet = false;

	public NFFRestrictSunGoal(INFFTamed mob) {
		super(mob, 1d);
		allowAllStates();
	}

	@Override
	public boolean checkCanUse() {
		if (isDisabled())
			return false;
		else if (!getPathfinder().level().isDay())
			return false;
		else if (!ignoreHelmet && !getPathfinder().getItemBySlot(EquipmentSlot.HEAD).isEmpty())
			return false;
		else if (mob instanceof INFFTamedSunSensitiveMob bssm && bssm.isSunImmune())
			return false;
		else if (this.mob.asMob().isInWater())
			return false;
		else if (!GoalUtils.hasGroundPathNavigation(getPathfinder()))
			return false;
		else return true;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (isAmphibious)
		{
			((INFFTamedAmphibious)mob).switchNav(false);			
		}
		if (GoalUtils.hasGroundPathNavigation(getPathfinder()))
			((GroundPathNavigation) getPathfinder().getNavigation()).setAvoidSun(true);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	@Override
	public void onStop() {
		if (GoalUtils.hasGroundPathNavigation(getPathfinder())) {
			((GroundPathNavigation) getPathfinder().getNavigation()).setAvoidSun(false);
		}
	}
}
