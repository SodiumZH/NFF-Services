package net.sodiumzh.nff.services.entity.ai.goal.preset;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

import java.util.EnumSet;

public class NFFFlyingFollowOwnerGoal extends NFFFlyingMoveGoal implements INFFFollowOwnerGoal
{
	public double teleportDistance = 12d;
	public double noFollowOnCombatDistance = 6d;
	public double minStartDistance = 4d;
	public double hoveringHeightOffset = 0d;

	public NFFFlyingFollowOwnerGoal(INFFTamed mob, double moveSpeed)
	{
		super(mob, moveSpeed);
		this.disallowAllStates();
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		this.allowState(NFFTamedMobAIState.FOLLOW);
	}

	public NFFFlyingFollowOwnerGoal(INFFTamed mob)
	{
		this(mob, 1.0D);
	}
	
	@Override
	public boolean checkCanUse()
	{
		if (mob.asMob().getMoveControl().hasWanted())
			return false;
		if (!mob.isOwnerPresent())
			return false;
		if (mob.asMob().getTarget() != null && mob.asMob().distanceToSqr(mob.getOwner()) < noFollowOnCombatDistance * noFollowOnCombatDistance)
			return false;
		if (mob.asMob().distanceToSqr(mob.getOwner()) < minStartDistance * minStartDistance)
			return false;
		else return true;
	}
	
	@Override
	public void onTick() {
		if (!mob.isOwnerPresent())
			return;	// Prevent potential nullptr crash
		this.goToOwnerPreset(this.getSpeedModifier());
	}	
	
	@Override
	public void moveToOwner(double param, Vec3 offset)
	{
		if (!goal().getMob().isOwnerInDimension())
			return;
		Mob mob = goal().getMob().asMob();
		Player owner = goal().getMob().getOwner();
		Vec3 pos = owner.getEyePosition();
		Vec3 offset1 = owner.position().subtract(mob.position());
		offset1 = new Vec3(offset1.x, 0, offset1.z).normalize().reverse().scale(0.5);	// keep a little distance to player
		this.flyTo(pos.x + offset.x + offset1.x, pos.y + offset.y + this.hoveringHeightOffset, pos.z + offset.z + offset1.z, param);
	}

	public NFFFlyingFollowOwnerGoal setTeleportDistance(double teleportDistance) {
		this.teleportDistance = teleportDistance;
		return this;
	}

	public NFFFlyingFollowOwnerGoal setNoFollowOnCombatDistance(double noFollowOnCombatDistance) {
		this.noFollowOnCombatDistance = noFollowOnCombatDistance;
		return this;
	}

	public NFFFlyingFollowOwnerGoal setMinStartDistance(double minStartDistance) {
		this.minStartDistance = minStartDistance;
		return this;
	}

	public NFFFlyingFollowOwnerGoal setHoveringHeightOffset(double hoveringHeightOffset) {
		this.hoveringHeightOffset = hoveringHeightOffset;
		return this;
	}
}
