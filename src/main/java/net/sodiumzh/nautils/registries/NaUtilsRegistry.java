package net.sodiumzh.nautils.registries;

import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.sodiumzh.nautils.eventhandler.NaUtilsSetupEventHandlers;
import net.sodiumzh.nautils.exceptions.DuplicateRegistryEntryException;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.registries.RegistryObject;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
/**
 * A simple registry. It's internally a {@link HashBiMap} with keys of {@link ResourceLocation}s.
 * Note that this is NOT a part of Minecraft registry system.
 */
public class NaUtilsRegistry<T>
{
    /** All declared registries. */
    private static final HashBiMap<ResourceLocation, NaUtilsRegistry<?>> REGISTRIES = HashBiMap.create();
    private final HashMap<ResourceLocation, Entry<T>> table = new HashMap<>();
    private boolean shouldGenerateOnSetup = false;
    private Class<?> valueClass;

    /**
     *
     * @param valueClass Expected class of the value. This class will not control the generic type of the registry,
     *                   and you must make sure this class is the same as the generic type, otherwise some issues will
     *                   happen (e.g. initialization phase).
     * @param registryKey Key of this registry in the table of all registries.
     */
    public NaUtilsRegistry(Class<?> valueClass, ResourceLocation registryKey)
    {
        REGISTRIES.put(registryKey, this);
        this.valueClass = valueClass;
    }

    public static Map<ResourceLocation, NaUtilsRegistry<?>> allRegistries()
    {
        return REGISTRIES;
    }

    public static NaUtilsRegistry<?> registryByKey(ResourceLocation key)
    {
        return REGISTRIES.get(key);
    }

    /**
     * Get this registry's key in the registry of all {@code NaUtilsRegistry}s.
     */
    public ResourceLocation getKeyOfRegistry()
    {
        return REGISTRIES.inverse().get(this);
    }

    public Class<?> getValueClass()
    {
        return valueClass;
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public boolean containsKey(ResourceLocation key) {
        return table.containsKey(key);
    }

    public boolean containsValue(T value)
    {
        for (var entry: this.table.values())
        {
            if (value.equals(entry.get())) return true;
        }
        return false;
    }

    /**
     * Get the value from key. Note that if the supplier throws an exception,
     * it will not crash but print stacktrace and return null.
     */
    public T getValue(ResourceLocation key) {
        Entry<T> entry = table.get(key);
        if (entry == null) return null;
        return entry.get();
    }

    public ResourceLocation getKey(T value) {
        for (var entry: this.table.entrySet())
        {
            if (value.equals(entry.getValue().get())) return entry.getKey();
        }
        return null;
    }

    public Set<ResourceLocation> keySet() {
        return table.keySet();
    }

    /**
     * Register an object from supplier.
     * @return An {@code Accessor} for getting the object, so that you can assign it to a
     * static field. Its usage is similar to {@link RegistryObject}.
     * <p>It's recommended to use {@link RegistryEntryCollection} instead (just like using {@link DeferredRegister}).
     * Directly registering may cause issues if the class in which you're registering objects is not loaded on setup phase.
     */
    public Accessor<T> register(ResourceLocation key, Supplier<T> supplier)
    {
        if (this.containsKey(key)) throw DuplicateRegistryEntryException.registeredTwice(key.toString());
        Entry entry = new Entry<>(supplier);
        this.table.put(key, entry);
        return new Accessor<>(entry);
    }

    /**
     * Only for {@link RegistryEntryCollection}.
     */
    void registerRaw(ResourceLocation key, Entry<T> value)
    {
        this.table.put(key, value);
    }

    /**
     * Regenerate the value of the given key, i.e. rerun the supplier and generate a new value.
     * <p><b>Take extreme care calling this.</b> This operation will probably generate a new value instance and may invalidate
     * the old references.
     */
    public void regenerateValue(ResourceLocation key)
    {
        this.table.get(key).regenerate();
    }

    /**
     * Regenerate all values, i.e. rerun all suppliers and generate new values.
     * <p><b>Take extreme care calling this.</b> This operation will probably generate new value instances and may invalidate
     * the old references.
     */
    public void regenerateAllValues()
    {
        this.table.keySet().forEach(key -> this.regenerateValue(key));
    }

    /**
     * Called only in {@link NaUtilsSetupEventHandlers#generateRegistries}.
     */
    public boolean shouldGenerateOnSetup()
    {
        return this.shouldGenerateOnSetup;
    }

    /**
     * Labels that this registry's values should be generated on setup phase (e.g. requiring data reading).
     * Registries with this label will generate values on {@link FMLCommonSetupEvent}.
     * @return {@code this}.
     */
    public NaUtilsRegistry<T> setShouldGenerateOnSetup()
    {
        this.shouldGenerateOnSetup = true;
        return this;
    }

    static class Entry<T>
    {
        private Supplier<T> supplier;
        private T cachedValue;

        public Entry(@Nonnull Supplier<T> supplier)
        {
            this.supplier = supplier;
            this.cachedValue = null;
        }

        /**
         * Get value from the supplier. Note that once the supplier output a valid value,
         * it won't rerun (i.e. the value won't change) until {@code regenerate} is called.
         */
        @Nullable
        public T get()
        {
            if (cachedValue == null) {
                try {
                    cachedValue = supplier.get();
                } catch (RuntimeException e)
                {
                    // If running supplier encountered error, don't crash but
                    // set the cache to null so that the supplier will rerun next time.
                    e.printStackTrace();
                    cachedValue = null;
                    return null;
                }
                return cachedValue;
            }
            else return cachedValue;
        }

        public void regenerate() {
            this.cachedValue = this.supplier.get();
        }
    }

    public static class Accessor<T>
    {
        private Entry<T> entry;
        private boolean validated;  // Labels whether this entry has been registered into a registry. If it's false, the get() will always returns null.

        public Accessor(Entry<T> entry) {
            this.entry = entry;
            this.validated = true;
        }

        public static <U> Accessor<U> invalid(Entry<U> entry)
        {
            Accessor<U> res = new Accessor<>(entry);
            res.validated = false;
            return res;
        }

        public T get()
        {
            if (!validated) return null;
            return entry.get();
        }

        Accessor<T> validate() {this.validated = true; return this;}
    }

}
