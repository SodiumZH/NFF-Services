package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.object.CastableObject;

import java.util.function.Function;

/**
 * Registered {@link Function}s.
 * <p>Note: the functions doesn't provide parameter and return types. You need to cast them yourself, and
 * ensure the classes match.
 */
public class NaUtilsFunctions {

    /**
     * Invoke the function with given key and input.
     * @param functionKey Registry key of the function.
     * @param param Input parameter.
     * @throws RuntimeException If any exception or error is thrown during function running, including class mismatch.
     * Note that if the key doesn't exist, it will return null and not throw exception.
     */
    public static CastableObject invoke(ResourceLocation functionKey, Object param)
    {
        Function<Object, Object> func = (Function<Object, Object>)NaUtilsRegistries.FUNCTIONS.getValue(functionKey);
        if (func == null) return null;
        try {
            return new CastableObject(func.apply(param));
        } catch (Exception | NoSuchFieldError | NoSuchMethodError t) {
            throw new RuntimeException(t);
        }
    }

}
