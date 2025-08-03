package net.sodiumzh.nfu.object;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@code CastableObject} is a nullable object wrapper that can be easily cast to any class without explicit casting.
 * It's generally for the methods invoked by reflection utils which originally return generic objects.
 * <p>Warning: Never directly cast the {@code CastableObject} instance to the target type like {@code (YourType)castableObject}!!
 * This syntax is not supported and will always cause a {@link ClassCastException}. Always use {@code castableObject.cast()}
 * or {@code castableObject.castTo(YourClass.class)}!
 */
public class CastableObject implements ICastable
{
	@Nullable
	private final Object obj;
	private static final CastableObject EMPTY = new CastableObject(null);

	public static CastableObject empty() { return EMPTY; }

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

	/**
	 * Cast to the target class if present and castable, or return fallback if not.
	 */
	@Nonnull
	public <T> T castOrElse(Class<T> clazz, T fallback) {
		return this.castOptional(clazz).orElse(fallback);
	}

	/**
	 * Cast to the target class if present and castable, or return fallback if not.
	 * <p>Context-based version. Type-safe, but may be slower than the class input version
	 * as it's implemented by exception catching.
	 */
	@Nonnull
	public <T> T castOrElse(T fallback) {
		return this.<T>castOptional().orElse(fallback);
	}
}
