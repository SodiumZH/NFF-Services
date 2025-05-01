package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.event.NFUEntityEvent;

/**
 * Fired on the beginning of {@code Entity#tick}, before {@code Entity#baseTick}.
 * NOT cancellable.
 */
public class EntityTickEvent extends NFUEntityEvent<Entity>
{
	public EntityTickEvent(Entity entity)
	{
		super(entity);
	}	
}
