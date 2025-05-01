package net.sodiumzh.nfu.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * An utility class as a templated {@link LivingEvent} that can automatically cast the entity to given class.
 */
public abstract class NFULivingEvent<T extends LivingEntity> extends LivingEvent
{

	public NFULivingEvent(T entity)
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
