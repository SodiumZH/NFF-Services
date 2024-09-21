package net.sodiumzh.nff.services.eventlisteners;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.mixin.events.entity.EntityTickEvent;

/**
 * @deprecated use {@link EntityTickEvent} instead
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
	
	public static class PreWorldTick extends ServerEntityTickEvent
	{
		public PreWorldTick(Entity entity)
		{
			super(entity);
		}
	}
	
	public static class PostWorldTick extends ServerEntityTickEvent
	{
		public PostWorldTick(Entity entity)
		{
			super(entity);
		}
	}
}
