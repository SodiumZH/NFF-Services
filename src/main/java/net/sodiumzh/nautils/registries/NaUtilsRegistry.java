package net.sodiumzh.nautils.registries;

import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.sodiumzh.nautils.eventhandler.NaUtilsSetupEventHandlers;
import net.sodiumzh.nautils.exceptions.DuplicateRegistryEntryException;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.registries.RegistryObject;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private final HashMap<ResourceLocation, Entry<? extends T>> table = new HashMap<>();
    private boolean shouldGenerateOnSetup = false;
    private int generateOnSetupPhase = 0;   // 0 = common setup: 1 = server setup; 2 = client setup

    /**
     * @param registryKey Key of this registry in the table of all registries.
     */
    public NaUtilsRegistry(ResourceLocation registryKey)
    {
        REGISTRIES.put(registryKey, this);
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
    @Nullable
    public T getValue(ResourceLocation key) {
        Entry<? extends T> entry = table.get(key);
        if (entry == null) return null;
        return entry.get();
    }

    @Nullable
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
    public <U extends T> Accessor<U> register(ResourceLocation key, Supplier<U> supplier)
    {
        if (this.containsKey(key)) throw DuplicateRegistryEntryException.registeredTwice(key.toString());
        Entry<U> entry = new Entry<>(supplier);
        this.table.put(key, entry);
        return new Accessor<>(entry);
    }

    /**
     * Only for {@link RegistryEntryCollection}.
     */
    void registerRaw(ResourceLocation key, Entry<? extends T> value)
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
        this.table.keySet().forEach(this::regenerateValue);
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
    public NaUtilsRegistry<T> setShouldGenerateOnCommonSetup()
    {
        this.shouldGenerateOnSetup = true;
        return this;
    }

    public NaUtilsRegistry<T> setShouldGenerateOnServerSetup()
    {
        this.shouldGenerateOnSetup = true;
        this.generateOnSetupPhase = 1;
        return this;
    }

    public NaUtilsRegistry<T> setShouldGenerateOnClientSetup()
    {
        this.shouldGenerateOnSetup = true;
        this.generateOnSetupPhase = 2;
        return this;
    }

    /**
     * Get which phase should this registry generate values.
     * 0 = common setup: 1 = client setup; 2 = server setup.
     * Note that if it {@code shouldGenerateOnSetup()} is false,
     * this value will be invalid.
     */
    public int getGenerateOnSetupPhase()
    {
        if (!this.shouldGenerateOnSetup())
            LogUtils.getLogger().warn(String.format("NaUtilsRegistry %s calling getGenerateOnSetupPhase, " +
                    "but shouldGenerateOnSetup() is false. Note that the result is invalid.", this.getKeyOfRegistry().toString()));
        return this.generateOnSetupPhase;
    }

    static class Entry<T>
    {
        private final Supplier<T> supplier;
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

    public static class Accessor<T> implements Supplier<T>
    {
        private Entry<T> entry;
        private boolean validated;  // Labels whether this entry has been registered into a registry. If it's false, the get() will always return null.

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
