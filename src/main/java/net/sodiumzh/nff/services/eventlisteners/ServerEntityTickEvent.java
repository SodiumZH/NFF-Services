package net.sodiumzh.nff.services.eventlisteners;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.mixin.events.entity.EntityTickEvent;

/**
 * @deprecated Use {@link EntityTickEvent} or {@link TickEvent.LevelTickEvent} instead
 */
@Deprecated
public class ServerEntityTickEvent extends Event {

	protected Entity entity;
	public Entity getEntity()
	{
		return entity;
	}
	
	public ServerEntityTickEvent(Entity entity)
	{
		this.entity = entity;
	}
	
	@Deprecated
	public static class PreWorldTick extends ServerEntityTickEvent
	{
		public PreWorldTick(Entity entity)
		{
			super(entity);
		}
	}
	
	@Deprecated
	public static class PostWorldTick extends ServerEntityTickEvent
	{
		public PostWorldTick(Entity entity)
		{
			super(entity);
		}
	}
}
