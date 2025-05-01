package net.sodiumzh.nfu.object;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
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
    @Nullable
    public default <T> T castTo(Class<T> clazz)
    {
        return cast();
    }

    /**
     * Directly get the raw object (in class {@code Object}). This operation is type-safe.
     */
    @Nullable
    public default Object get()
    {
        return castTarget().get();
    }

    /**
     * Cast into an {@link Optional} if castable. Return empty if type mismatch or this is empty.
     * <p>This operation is type-safe.
     */
    public default <T> Optional<T> castOptional(Class<T> clazz) {
        Object res = castTarget().get();
        if (res == null) return Optional.empty();
        if (clazz.isAssignableFrom(res.getClass())) return Optional.ofNullable((T) res);
        else return Optional.empty();
    }

    /**
     * Cast into an {@link Optional} if castable. Return empty if type mismatch or this is empty.
     * <p>This operation is type-safe, but may be slower than the version with class explicitly defined,
     * as this is implemented by exception catching.
     */
    public default <T> Optional<T> castOptional() {
        try {
            return Optional.ofNullable(this.cast());
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }
}
