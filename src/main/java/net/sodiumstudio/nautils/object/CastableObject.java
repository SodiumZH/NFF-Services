package net.sodiumstudio.nautils.object;

import java.util.function.Consumer;

import javax.annotation.Nullable;

/**
 * A {@code CastableObject} is an object wrapper that can be easily cast to any class without explicit casting.
 * It's generally for the methods invoked by reflection utils which originally return generic objects.`
 */
public class CastableObject
{
	@Nullable
	private final Object obj;
	
	public CastableObject(Object obj)
	{
		this.obj = obj;
	}
	
	/**
	 * Implicitly cast the object to the target class, referred by context.
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T cast()
	{
		try {
			return (T) obj;
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("CastableObject casting failed: object class " + obj.getClass().getSimpleName());
		} 
	}
	
	/**
	 * Explicitly cast the object to the target class specified by the parameter.
	 */
	public <T> T castTo(Class<T> clazz)
	{
		return cast();
	}
	
	
	/**
	 * Directly get the raw object (in class {@code Object}).
	 */
	@Nullable
	public Object get()
	{
		return obj;
	}
	
	public boolean isPresent()
	{
		return obj != null;
	}
}
