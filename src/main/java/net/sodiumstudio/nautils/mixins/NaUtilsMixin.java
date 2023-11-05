package net.sodiumstudio.nautils.mixins;

import com.mojang.logging.LogUtils;

/**
 * Base interface for all mixins in NaUtils containing some common utilities.
 * @param <T> Mixin target class.
 */
public interface NaUtilsMixin<T> {
	
	/**
	 * Get the caller object.
	 */
	@SuppressWarnings({ "unchecked", "cast" })
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
}
