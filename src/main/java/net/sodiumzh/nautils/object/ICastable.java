package net.sodiumzh.nautils.object;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Interface for something that can be easily cast to any class without explicit casting.
 * Override {@code castTarget} to select cast target.
 */
public interface ICastable {

    @Nonnull
    public default Supplier<?> castTarget() {
        return () -> this;
    }

    /**
     * Implicitly cast the object to the target class, referred by context.
     */
    public default <T> T cast()
    {
        Object obj = castTarget().get();
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
    public default <T> T castTo(Class<T> clazz)
    {
        return cast();
    }

    /**
     * Directly get the raw object (in class {@code Object}).
     */
    @Nullable
    public default Object get()
    {
        return castTarget().get();
    }
}
