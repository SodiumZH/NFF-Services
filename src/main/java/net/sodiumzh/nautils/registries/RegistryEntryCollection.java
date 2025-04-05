package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nautils.containers.Tuple2;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Defines a collection of entries that should be registered into a
 * given registry ({@link NaUtilsRegistry}). Note that the keys are <i>paths</i> of the resource
 * location while the mod id (namespace) is predefined.
 * <p>It's usage is similar to {@link DeferredRegister}s, and use {@code merge} in mod loading e.g. {@link FMLCommonSetupEvent} to
 * merge the entries into the registry.
 * <p>Note: Do not use {@code merge} in mod main class's constructor. {@link NaUtilsRegistry} doesn't use suppliers as values,
 * and it's not really deferred-registered. Therefore it must be registered <i>after</i> Forge registries, so that
 * the Forge registry entries are ensured to be valid.
 */
public class RegistryEntryCollection<T>
{
    private final NaUtilsRegistry<T> registry;
    private final String namespace;
    private final HashMap<ResourceLocation, Tuple2<NaUtilsRegistry.Entry<? extends T>, NaUtilsRegistry.Accessor<? extends T>>> table = new HashMap<>();
    private RegistryEntryCollection(NaUtilsRegistry<T> registry, String namespace)
    {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <U> RegistryEntryCollection<U> create(NaUtilsRegistry<U> registry, String namespace)
    {
        return new RegistryEntryCollection<>(registry, namespace);
    }

    /**
     * Register an object to the collection.
     * This is the same as {@code put}, but returns the input value itself
     * so that you can assign the value to a static field together with registering.
     */
    public <U extends T> NaUtilsRegistry.Accessor<U> register(@Nonnull String key, @Nonnull Supplier<U> value)
    {
        NaUtilsRegistry.Entry<U> entry = new NaUtilsRegistry.Entry<>(registry, value, new ResourceLocation(namespace, key));
        NaUtilsRegistry.Accessor<U> accessor = NaUtilsRegistry.Accessor.createInvalid(entry);
        this.table.put(new ResourceLocation(namespace, key), new Tuple2<>(entry, accessor));
        return new NaUtilsRegistry.Accessor<>(entry);
    }

    public boolean hasKey(ResourceLocation key)
    {
        return this.table.containsKey(key);
    }

    public void merge()
    {
        this.table.forEach((key, value) -> {
            this.registry.registerRaw(key, value.getA());
            value.getB().validate();
        });
    }
}

