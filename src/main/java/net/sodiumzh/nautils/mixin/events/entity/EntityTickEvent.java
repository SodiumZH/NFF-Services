package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
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
