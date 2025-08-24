package net.sodiumzh.nfu.reflection;

import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.object.CastableObject;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility for frequent reflective access of a field. It caches whether the field exists and
 * the {@link Field} instance for each accessing class, and prevents frequent unsafe {@link Class#getDeclaredMethod} operations.
 */
public class CachedMethodAccessor {

    private final Map<Class<?>, Optional<Method>> map = new ConcurrentHashMap<>();
    private final String name;
    private final Class<?>[] argTypes;

    public CachedMethodAccessor(String name, boolean remap, Class<?>... argTypes) {
        this.argTypes = argTypes;
        if (remap)
            this.name = NFUReflectionStatics.remapMethodName(name);
        else this.name = name;
    }

    @Nonnull
    public Optional<Method> getOptionalMethod(Class<?> clazz) {
        return map.computeIfAbsent(clazz, k -> {
            var v = NFUReflectionStatics.findMethodIfDeclared(k, this.name, argTypes);
            v.ifPresent(f -> f.setAccessible(true));
            return v;
        });
    }

    /**
     * Invoke the method if present in the given declared class. Do nothing if absent.
     * @return A {@link CastableObject} containing the method return (empty-able). Empty if the method is absent.
     */
    public CastableObject invokeIfPresent(Object obj, Class<?> declaredClass, Object... args) {
        Method method = this.getOptionalMethod(declaredClass).orElse(null);
        if (method == null) return CastableObject.empty();
        try {
            return new CastableObject(method.invoke(obj, args));
        } catch (InvocationTargetException e) {
            throw new ReflectionFailedException(e.getTargetException());
        } catch (Exception e) {
            throw new ReflectionFailedException(e);
        }
    }

}
