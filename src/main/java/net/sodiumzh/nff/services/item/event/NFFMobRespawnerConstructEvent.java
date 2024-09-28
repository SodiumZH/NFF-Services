package net.sodiumzh.nff.services.item.event;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

public class NFFMobRespawnerConstructEvent extends Event
{
	protected Mob mob;
	protected NFFMobRespawnerInstance respawner;
	//protected ItemStack stack;
	
	public Mob getMob() {return mob;};
	public NFFMobRespawnerInstance getRespawner() {return respawner;};
	//public ItemStack getStack() {return stack;}
	
	public NFFMobRespawnerConstructEvent(Mob mob, NFFMobRespawnerInstance respawner)
	{
		this.mob = mob;
		this.respawner = respawner;
		//this.stack = stack;
	}
	
	public static class Before extends NFFMobRespawnerConstructEvent
	{
		public Before(Mob mob, NFFMobRespawnerInstance respawner)
		{
			super(mob, respawner);
		}
	}
	
	public static class After extends NFFMobRespawnerConstructEvent
	{
		
		public After(Mob mob, NFFMobRespawnerInstance respawner)
		{
			super(mob, respawner);
		}
	}
}
