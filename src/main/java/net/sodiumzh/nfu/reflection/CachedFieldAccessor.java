package net.sodiumzh.nfu.reflection;

import joptsimple.internal.Reflection;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.object.CastableObject;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility for frequent reflective access of a field. It caches whether the field exists and
 * the {@link Field} instance for each accessing class, and prevents frequent unsafe {@link Class#getDeclaredField} operations.
 */
public class CachedFieldAccessor {

    private final Map<Class<?>, Optional<Field>> map = new ConcurrentHashMap<>();
    private final String name;

    public CachedFieldAccessor(String name, boolean remapped) {
        if (remapped)
            this.name = NFUReflectionStatics.remapFieldName(name);
        else this.name = name;
    }

    public Optional<Field> getOptionalField(Class<?> declaredClass) {
        this.cacheFieldIfAbsent(declaredClass);
        map.get(declaredClass).ifPresent(fld -> fld.setAccessible(true));
        return map.get(declaredClass);
    }

    /**
     * Get the field value if present. If the field is absent or the value is {@code null}, returns {@link CastableObject#empty()}.
     */
    public CastableObject getValue(Object obj, Class<?> declaredClass) {
        this.cacheFieldIfAbsent(declaredClass);
        return map.get(declaredClass).map(fld -> {
            try {
                fld.setAccessible(true);
                return new CastableObject(fld.get(obj));
            } catch (Exception e) {
                throw new ReflectionFailedException(e);
            }
        }).map(CastableObject::new).orElse(CastableObject.empty());
    }

    /**
     * Get the field value if present. Do nothing if the field is absent.
     */
    public void setValue(Object obj, Class<?> declaredClass, Object value) {
        this.cacheFieldIfAbsent(declaredClass);
        map.get(declaredClass).ifPresent(fld -> {
            try {
                fld.set(obj, value);
            } catch (Exception e) {
                throw new ReflectionFailedException(e);
            }
        });
    }

    private void cacheFieldIfAbsent(Class<?> clz) {
        map.computeIfAbsent(clz, k -> {
            var v = NFUReflectionStatics.findFieldIfDeclared(k, this.name);
            v.ifPresent(f -> f.setAccessible(true));
            return v;
        });
    }

}
