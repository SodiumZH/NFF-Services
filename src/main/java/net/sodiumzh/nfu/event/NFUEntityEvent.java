package net.sodiumzh.nfu.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * An utility class as a templated {@link EntityEvent} that can automatically cast the entity to given class.
 */
public abstract class NFUEntityEvent<T extends Entity> extends EntityEvent
{
	public NFUEntityEvent(T entity)
	{
		super(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getEntity()
	{
		return (T)(super.getEntity());
	}
}
