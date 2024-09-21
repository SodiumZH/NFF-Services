package net.sodiumzh.nff.services.entity.ai.goal.presets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.sodiumzh.nautils.statics.NaUtilsLevelStatics;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFFlyingLandGoal extends NFFFlyingMoveGoal
{

	public NFFFlyingLandGoal(INFFTamed mob, double speed)
	{
		super(mob, speed);
		this.disallowAllStates();
		this.allowState(WAIT);
	}

	public NFFFlyingLandGoal(INFFTamed mob)
	{
		this(mob, 0.25d);
	}
	
	@Override
	public boolean checkCanUse() {
		Level level = mob.asMob().level();
		if (mob.asMob().getMoveControl().hasWanted())
			return false;
		if (!level.getBlockState(mob.asMob().blockPosition().below()).isAir())
			return false;
		if (NaUtilsLevelStatics.isAboveVoid(mob.asMob().blockPosition(), mob.asMob()))
			return false;
		if (mob.asMob().getTarget() != null)
			return false;
		else return true;		
	}

	@Override
	public void onTick()
	{
		if (!mob.isOwnerPresent())
			return;	// Prevent potential nullptr crash
		if (mob.asMob().getMoveControl().hasWanted())
			return;
		if (NaUtilsLevelStatics.isAboveVoid(mob.asMob().blockPosition(), mob.asMob()))
			return;
		BlockPos pos = mob.asMob().blockPosition();
		while (mob.asMob().level().getBlockState(pos).isAir() && pos.getY() >= mob.asMob().level().getMinBuildHeight())
			pos = pos.below();
		pos = pos.above();
		mob.asMob().getMoveControl().setWantedPosition(pos.getX(), pos.getY(), pos.getZ(), speed);
	}
	
}
