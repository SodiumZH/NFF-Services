package net.sodiumzh.nff.services.eventlisteners;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.capability.CNFFTamable;

public class TamableTimerUpEvent extends Event
{
	protected Mob mob;
	protected CNFFTamable cap;
	protected String key;
	protected Player player;
	
	public TamableTimerUpEvent(CNFFTamable mobCap, String key, @Nullable Player player)
	{
		cap = mobCap;
		mob = mobCap.getOwner();
		this.key = key;
		this.player = player;
	}
	
	public Mob getMob()
	{
		return mob;
	}
	
	public CNFFTamable getCapability()
	{
		return cap;
	}
	
	public String getKey()
	{
		return key;
	}
	
	@Nullable
	public Player getPlayer()
	{
		return player;
	}
}
