package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.ai.goal.NFFMoveGoal;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

/**
 * Movement base for FlyingMob. No PathfinderMob.
 */
public abstract class NFFFlyingMoveGoal extends NFFMoveGoal
{
	public double speed = 0.25d;
	
	
	public NFFFlyingMoveGoal(INFFTamed mob, double speed)
	{
		super(mob);
		this.speed = speed;
		flyOnly();		
	}
	
	public NFFFlyingMoveGoal(INFFTamed mob)
	{
		this(mob, 0.25d);
	}

	public void flyTo(Vec3 targetPos, double speed)
	{
		MoveControl control = mob.asMob().getMoveControl();
		control.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speed);
	}
	
	/* Util */
		
	public Vec3 getWantedMovementVector()
	{
		if (mob.asMob().getMoveControl() == null || !mob.asMob().getMoveControl().hasWanted())
			return Vec3.ZERO;
		double deltaX = mob.asMob().getMoveControl().getWantedX() - mob.asMob().getX();
		double deltaY = mob.asMob().getMoveControl().getWantedY() - mob.asMob().getY();
		double deltaZ = mob.asMob().getMoveControl().getWantedZ() - mob.asMob().getZ();
		return new Vec3(deltaX, deltaY, deltaZ);
	}
	
	public double distSqrToOwner()
	{
		if (!mob.isOwnerPresent())
			return 0;
		else return mob.asMob().distanceToSqr(mob.getOwner());
	}
	
	protected double getActualSpeed()
	{
		if (mob.asMob().getAttribute(Attributes.FLYING_SPEED) != null)
			return speed * mob.asMob().getAttributeValue(Attributes.FLYING_SPEED) / mob.asMob().getAttributeBaseValue(Attributes.FLYING_SPEED);
		else if (mob.asMob().getAttribute(Attributes.MOVEMENT_SPEED) != null)
			return speed * mob.asMob().getAttributeValue(Attributes.MOVEMENT_SPEED) / mob.asMob().getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
		else return speed;
	}
}
