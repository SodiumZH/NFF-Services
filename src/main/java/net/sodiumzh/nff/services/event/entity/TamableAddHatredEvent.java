package net.sodiumzh.nff.services.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.TamableHatredReason;

@Cancelable
public class TamableAddHatredEvent extends Event
{
	public final Mob mob;
	public final Player toAdd;
	public int ticks;
	public final TamableHatredReason reason;
	public final boolean isPermanent;

	public TamableAddHatredEvent(Mob mob, Player toAdd, int ticks, TamableHatredReason reason)
	{
		this.mob = mob;
		this.toAdd = toAdd;
		this.ticks = ticks;
		this.reason = reason;
		this.isPermanent = ticks < 0;
	}
}
