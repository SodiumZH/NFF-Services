package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Posted before a {@link Monster} is preventing player sleep.
 * <p>Cancellable. If cancelled, this monster will not prevent sleep.
 */
@Cancelable
public class MonsterPreventSleepEvent extends NFULivingEvent<Monster>
{
	private final ServerPlayer player;
	
	public MonsterPreventSleepEvent(Monster entity, ServerPlayer player)
	{
		super(entity);
		this.player = player;
	}
	
	public ServerPlayer getPlayer()
	{
		return player;
	}

}
