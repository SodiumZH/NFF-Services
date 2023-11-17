package net.sodiumstudio.nautils.events;

import net.minecraftforge.eventbus.api.Event;

/**
 * An utility class of a simple event with a specified object.
 */
public class NaUtilsObjectEvent<T> extends Event
{
	protected final T object;
	
	public NaUtilsObjectEvent (T obj)
	{
		this.object = obj;
	}
	
	public T getObject()
	{
		return object;
	}
}
