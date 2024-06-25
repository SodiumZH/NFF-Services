package net.sodiumstudio.nautils.mixins;

import com.mojang.logging.LogUtils;

/**
 * Base interface for all mixins in NaUtils containing some common utilities.
 * <p> Note: multi-class mixin can also use this interface, but the {@code get()} can get only as the template class. 
 * If class mismatches it will throw exception. Use {@code cast()} to cast to any classes (mismatch = exception).
 * @param <T> Mixin target class.
 */
public interface NaUtilsMixin<T> {
	
	/**
	 * Get the caller object.
	 */
	@SuppressWarnings("unchecked")
	public default T get()
	{
		try
		{
			return (T)((Object)this);
		} catch (ClassCastException e)
		{
			LogUtils.getLogger().error("NaUtils Mixin error: class mismatch.");
			throw e;
		}
	}
	
	/**
	 * Cast the caller object to any class.
	 */
	@SuppressWarnings("unchecked")
	public default <U> U cast()
	{
		try
		{
			return (U)(this.get());
		} catch (ClassCastException e)
		{
			LogUtils.getLogger().error("NaUtils Mixin error: class mismatch.");
			throw e;
		}
	}
	
}
