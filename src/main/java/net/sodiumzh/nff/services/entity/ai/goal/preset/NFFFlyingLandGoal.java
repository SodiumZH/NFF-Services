package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.util.NFULevelStatics;

import java.util.EnumSet;

public class NFFFlyingLandGoal extends NFFFlyingMoveGoal
{

	protected double heightOffset = 0d;

	public NFFFlyingLandGoal(INFFTamed mob, double speed)
	{
		super(mob, speed);
		this.setFlags(EnumSet.of(Flag.MOVE));
		this.disallowAllStates();
		this.allowState(WAIT);
	}

	public NFFFlyingLandGoal(INFFTamed mob)
	{
		this(mob, 1.0d);
	}
	
	@Override
	public boolean checkCanUse() {
		Level level = mob.asMob().level;
		if (mob.asMob().getMoveControl().hasWanted())
			return false;
		if (!level.getBlockState(mob.asMob().blockPosition().below()).isAir())
			return false;
		if (NFULevelStatics.isAboveVoid(mob.asMob().blockPosition(), mob.asMob()))
			return false;
		if (mob.asMob().getTarget() != null)
			return false;
		else return true;		
	}

	@Override
	public void onTick()
	{
		if (!mob.isOwnerInDimension())
			return;	// Prevent potential nullptr crash
		if (mob.asMob().getMoveControl().hasWanted())
			return;
		if (NFULevelStatics.isAboveVoid(mob.asMob().blockPosition(), mob.asMob()))
			return;
		BlockPos pos = mob.asMob().blockPosition();
		while (mob.asMob().level.getBlockState(pos).isAir() && pos.getY() >= mob.asMob().level.getMinBuildHeight())
			pos = pos.below();
		pos = pos.above();
		this.flyTo(new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5d, heightOffset + 0.5d, 0.5d), this.getSpeedModifier());
	}

	public double getHeightOffset() {
		return heightOffset;
	}

	public NFFFlyingLandGoal setHeightOffset(double heightOffset) {
		this.heightOffset = heightOffset;
		return this;
	}
}
