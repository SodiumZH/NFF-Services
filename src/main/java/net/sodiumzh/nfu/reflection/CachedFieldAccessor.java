package net.sodiumzh.nfu.reflection;

import joptsimple.internal.Reflection;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.object.CastableObject;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A utility for frequent reflective access of a field. It caches whether the field exists and
 * the {@link Field} instance for each accessing class, and prevents frequent unsafe {@link Class#getField} operations.
 */
public class CachedFieldAccessor {

    private final Map<Class<?>, Optional<Field>> map = new HashMap<>();
    private final String name;

    public CachedFieldAccessor(String name, boolean remapped) {
        if (remapped)
            this.name = NFUReflectionStatics.remapFieldName(name);
        else this.name = name;
    }

    public CastableObject getValue(Object obj, Class<?> declaredClass) {
        if (!map.containsKey(declaredClass)) {
            map.put(declaredClass, NFUReflectionStatics.findFieldIfDeclared(declaredClass, this.name));
        }
        return map.get(declaredClass).map(fld -> {
            try {
                return new CastableObject(fld.get(obj));
            } catch (Exception e) {
                throw new ReflectionFailedException(e);
            }
        }).map(CastableObject::new).orElse(CastableObject.empty());
    }

    public void setValue(Object obj, Class<?> declaredClass, Object value) {
        if (!map.containsKey(declaredClass)) {
            map.put(declaredClass, NFUReflectionStatics.findFieldIfDeclared(declaredClass, this.name));
        }
        map.get(declaredClass).ifPresent(fld -> {
            try {
                fld.set(obj, value);
            } catch (Exception e) {
                throw new ReflectionFailedException(e);
            }
        });
    }

}
