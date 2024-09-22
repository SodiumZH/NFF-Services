package net.sodiumzh.nautils;

import org.apache.commons.lang3.mutable.MutableObject;

/**
 * A class to hide variable inside in order to 
 * enable changing in final variables or lambda functions
 * @param <T> Type of wrapped variable
 * @deprecated Use {@link MutableObject} instead
 */
@Deprecated
public class Wrapped<T> extends MutableObject<T> {
	
	public Wrapped()
	{
		super();
	}
	
	public Wrapped(T value)
	{
		super(value);
	}
	
	public T get()
	{
		return this.getValue();
	}
	
	public void set(T value)
	{
		this.setValue(value);
	}
	
	public Wrapped<T> valueOf(T val)
	{
		return new Wrapped<T>(val);
	}
	
	/**
	 * @deprecated Use {@code MutableObject<Boolean>} instead
	 */
	@Deprecated
	public static class Boolean
	{
		private boolean value;
		public Boolean(boolean value)
		{
			this.value = value;
		}
		
		public boolean get()
		{
			return value;
		}
		
		public void set(boolean value)
		{
			this.value = value;
		}
		
		public static Wrapped.Boolean valueOf(boolean val)
		{
			return new Wrapped.Boolean(val);
		}
		
	}
}
