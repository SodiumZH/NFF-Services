package net.sodiumzh.nfu.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sodiumzh.nfu.container.Tuple2;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Defines a collection of entries that should be registered into a
 * given registry ({@link NFURegistry}). Note that the keys are <i>paths</i> of the resource
 * location while the mod id (namespace) is predefined.
 * <p>It's usage is similar to {@link DeferredRegister}s, and use {@code merge} in mod main class to add
 * the entries to registry. Like {@link DeferredRegister}, it also uses suppliers.
 */
public class NFURegistryEntryCollection<T>
{
    private final NFURegistry<T> registry;
    private final String namespace;
    private final HashMap<ResourceLocation, Tuple2<NFURegistry.Entry<? extends T>, NFURegistry.Accessor<? extends T>>> table = new HashMap<>();
    private NFURegistryEntryCollection(NFURegistry<T> registry, String namespace)
    {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <U> NFURegistryEntryCollection<U> create(NFURegistry<U> registry, String namespace)
    {
        return new NFURegistryEntryCollection<>(registry, namespace);
    }

    /**
     * Register an object to the collection.
     * This is the same as {@code put}, but returns the input value itself
     * so that you can assign the value to a static field together with registering.
     */
    public <U extends T> NFURegistry.Accessor<U> register(@Nonnull String key, @Nonnull Supplier<U> value)
    {
        NFURegistry.Entry<U> entry = new NFURegistry.Entry<>(registry, value, new ResourceLocation(namespace, key));
        NFURegistry.Accessor<U> accessor = NFURegistry.Accessor.createInvalid(entry);
        this.table.put(new ResourceLocation(namespace, key), new Tuple2<>(entry, accessor));
        return new NFURegistry.Accessor<>(entry);
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

