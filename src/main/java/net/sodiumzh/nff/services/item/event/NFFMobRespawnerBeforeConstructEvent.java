package net.sodiumzh.nff.services.item.event;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

public class NFFMobRespawnerBeforeConstructEvent extends Event
{
	private Mob mob;
	private NFFMobRespawnerInstance respawner;
	//protected ItemStack stack;
	
	public Mob getMob() {return mob;};
	public NFFMobRespawnerInstance getRespawner() {return respawner;};
	//public ItemStack getStack() {return stack;}
	
	public NFFMobRespawnerBeforeConstructEvent(Mob mob, NFFMobRespawnerInstance respawner)
	{
		this.mob = mob;
		this.respawner = respawner;
	}

}
