package net.sodiumzh.nff.services.item.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

public class NFFMobRespawnerFinishRespawnEvent extends Event
{
	protected Mob mob;
	protected Player player;
	protected NFFMobRespawnerInstance resp;

	public Mob getMob() {return mob;}
	public Player getPlayer() {return player;}
	public NFFMobRespawnerInstance getRespawner() {return resp;}
	
	public NFFMobRespawnerFinishRespawnEvent(Mob mob, Player player, NFFMobRespawnerInstance resp)
	{
		this.mob = mob;
		this.player = player;
		this.resp = resp;
	}
}
