package net.sodiumzh.nff.services.event.entity;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFMobTamedEvent extends Event
{
	public final Mob mobBefore;
	
	public final INFFTamed mobBefriended;
	
	public NFFMobTamedEvent(Mob before, INFFTamed after)
	{
		this.mobBefore = before;
		this.mobBefriended = after;
	}
}
