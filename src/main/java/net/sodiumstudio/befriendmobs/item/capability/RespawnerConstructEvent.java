package net.sodiumstudio.befriendmobs.item.capability;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class RespawnerConstructEvent extends Event
{
	protected Mob mob;
	protected CMobRespawner respawner;
	//protected ItemStack stack;
	
	public Mob getMob() {return mob;};
	public CMobRespawner getRespawner() {return respawner;};
	//public ItemStack getStack() {return stack;}
	
	public RespawnerConstructEvent(Mob mob, CMobRespawner respawner)
	{
		this.mob = mob;
		this.respawner = respawner;
		//this.stack = stack;
	}
	
	public static class Before extends RespawnerConstructEvent
	{
		public Before(Mob mob, CMobRespawner respawner)
		{
			super(mob, respawner);
		}
	}
	
	public static class After extends RespawnerConstructEvent
	{
		public final ItemStack stackConstructed;
		
		public After(Mob mob, CMobRespawner respawner, ItemStack stackConstructed)
		{
			super(mob, respawner);
			this.stackConstructed = stackConstructed;
		}
	}
}
