package net.sodiumstudio.nautils;


/**
 * A class to hide variable inside in order to 
 * enable changing in final variables or lambda functions
 * @param <T> Type of wrapped variable
 */
public class Wrapped<T> {
	
	private T value;
	
	public Wrapped()
	{
		this(null);
	}
	
	public Wrapped(T value)
	{
		this.value = value;
	}
	
	public T get()
	{
		return value;
	}
	
	public void set(T value)
	{
		this.value = value;
	}
	
	public Wrapped<T> valueOf(T val)
	{
		return new Wrapped<T>(val);
	}
	
	/**
	 * @deprecated Use {@code Wrapped<Boolean>} instead
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
