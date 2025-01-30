package net.sodiumzh.nff.services.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFMobTamedEvent extends Event
{
	public final Mob mobBefore;
	
	public final Mob mobBefriended;
	
	public NFFMobTamedEvent(Mob before, Mob after)
	{
		this.mobBefore = before;
		this.mobBefriended = after;
	}
}
