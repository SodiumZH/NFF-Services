package net.sodiumzh.nfu.mixin;

import com.mojang.logging.LogUtils;
import net.sodiumzh.nfu.object.ICastable;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Base interface for all mixins in NFU containing some common utilities.
 * <p>Note: multi-class mixin can also use this interface, but the {@code caller()} can get only as the template class. 
 * If class mismatches it will throw exception. Use {@code cast()} to cast to any classes (mismatch = exception).
 * @param <T> Mixin target class.
 */
public interface NFUMixin<T> extends ICastable {
	
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
			LogUtils.getLogger().error("NFU Mixin error: class mismatch.");
			throw e;
		}
	}

	@Override
	@Nonnull
	public default Supplier<?> castTarget() {
		return this::caller;
	}

}
