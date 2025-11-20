package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.world.entity.PathfinderMob;
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
	public NFFFlyingMoveGoal(INFFTamed mob, double speed)
	{
		super(mob, speed);
		flyOnly();		
	}
	
	public NFFFlyingMoveGoal(INFFTamed mob)
	{
		this(mob, 1.0d);
	}

	public void flyTo(Vec3 targetPos, double speed)
	{
		// Use navigation if pathfinding, otherwise use move control
		if (mob.asMob() instanceof PathfinderMob pm && this.shouldUseNavigation()) {
			pm.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, speed);
		}
		else {
			MoveControl control = mob.asMob().getMoveControl();
			control.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speed);
		}
	}

	public final void flyTo(double x, double y, double z, double speed) {
		this.flyTo(new Vec3(x, y, z), speed);
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
		if (!mob.isOwnerInDimension())
			return 0;
		else return mob.asMob().distanceToSqr(mob.getOwnerInDimension());
	}

}
