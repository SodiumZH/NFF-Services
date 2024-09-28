package net.sodiumzh.nff.services.item.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;

@Cancelable
public class NFFMobRespawnerStartRespawnEvent extends Event
{
	protected NFFMobRespawnerInstance resp;
	protected Player player;
	protected BlockPos pos;
	protected Direction dir;

	public NFFMobRespawnerInstance getRespawner() {return resp;}
	public Player getPlayer() {return player;}
	public BlockPos getBlockPos() {return pos;}
	public Direction getDirection() {return dir;}

	public NFFMobRespawnerStartRespawnEvent(NFFMobRespawnerInstance resp, Player player, BlockPos pos, Direction dir)
	{
		this.resp = resp;
		this.player = player;
		this.pos = pos;
		this.dir = dir;		
	}
}
