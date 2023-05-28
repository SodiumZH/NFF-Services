package net.sodiumstudio.befriendmobs.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class MobRespawnerFinishRespawnEvent extends Event
{
	protected Mob mob;
	protected Player player;
	protected MobRespawnerInstance resp;

	public Mob getMob() {return mob;}
	public Player getPlayer() {return player;}
	public MobRespawnerInstance getRespawner() {return resp;}
	
	public MobRespawnerFinishRespawnEvent(Mob mob, Player player, MobRespawnerInstance resp)
	{
		this.mob = mob;
		this.player = player;
		this.resp = resp;
	}
}
