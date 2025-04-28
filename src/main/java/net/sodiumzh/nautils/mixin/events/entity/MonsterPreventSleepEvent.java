package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nautils.events.NaUtilsLivingEvent;

/**
 * Posted before a {@link Monster} is preventing player sleep.
 * <p>Cancellable. If cancelled, this monster will not prevent sleep.
 */
@Cancelable
public class MonsterPreventSleepEvent extends NaUtilsLivingEvent<Monster>
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
