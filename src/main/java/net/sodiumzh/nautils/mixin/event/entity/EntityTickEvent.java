package net.sodiumzh.nautils.mixin.event.entity;

import net.minecraft.world.entity.Entity;
import net.sodiumzh.nautils.events.NaUtilsEntityEvent;

/**
 * Fired on the beginning of {@code Entity#tick}, before {@code Entity#baseTick}.
 * NOT cancellable.
 */
public class EntityTickEvent extends NaUtilsEntityEvent<Entity>
{
	public EntityTickEvent(Entity entity)
	{
		super(entity);
	}	
}
