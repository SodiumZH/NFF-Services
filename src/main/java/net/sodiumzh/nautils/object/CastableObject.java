package net.sodiumzh.nautils.object;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@code CastableObject} is an object wrapper that can be easily cast to any class without explicit casting.
 * It's generally for the methods invoked by reflection utils which originally return generic objects.
 * <p>Warning: Never directly cast the {@code CastableObject} instance to the target type like {@code (YourType)castableObject}!!
 * This syntax is not supported and will always cause a {@link ClassCastException}. Always use {@code castableObject.cast()}
 * or {@code castableObject.castTo(YourClass.class)}!
 */
public class CastableObject implements ICastable
{
	@Nullable
	private final Object obj;
	
	public CastableObject(@Nullable Object obj)
	{
		this.obj = obj;
	}

	@Override
	@Nonnull
	public Supplier<?> castTarget() {
		return () -> obj;
	}
	
	public boolean isPresent()
	{
		return obj != null;
	}
}
