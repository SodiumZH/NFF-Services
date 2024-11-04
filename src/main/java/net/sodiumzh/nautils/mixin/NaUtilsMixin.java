package net.sodiumzh.nautils.mixin;

import com.mojang.logging.LogUtils;
import net.sodiumzh.nautils.object.ICastable;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Base interface for all mixins in NaUtils containing some common utilities.
 * <p>Note: multi-class mixin can also use this interface, but the {@code caller()} can get only as the template class. 
 * If class mismatches it will throw exception. Use {@code cast()} to cast to any classes (mismatch = exception).
 * @param <T> Mixin target class.
 */
public interface NaUtilsMixin<T> extends ICastable {
	
	/**
	 * Get the caller object.
	 */
	@SuppressWarnings("unchecked")
	public default T caller()
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

	@Override
	@Nonnull
	public default Supplier<?> castTarget() {
		return this::caller;
	}

}
