package net.sodiumzh.nautils.object;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@code CastableObject} is an object wrapper that can be easily cast to any class without explicit casting.
 * It's generally for the methods invoked by reflection utils which originally return generic objects.`
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
