package net.sodiumstudio.nautils.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired on the beginning of {@code Entity#tick}. If cancelled, the whole {@code tick} will be skipped.
 */
@Cancelable
public class EntityTickEvent extends EntityEvent
{
	public EntityTickEvent(Entity entity)
	{
		super(entity);
	}	
}
