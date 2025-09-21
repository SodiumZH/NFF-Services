package net.sodiumzh.nfu.registry;

import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.DeferredRegister;
import net.sodiumzh.nfu.eventhandler.NFUSetupEventHandlers;
import net.sodiumzh.nfu.exception.DuplicateRegistryEntryException;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import net.sodiumzh.nfu.object.DirectedGraphNode;
import net.sodiumzh.nfu.object.LimitedMutable;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirector;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirectorEventListeners;
import net.sodiumzh.nfu.util.NFUDebugStatics;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.registries.RegistryObject;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
/**
 * A simple registry. It's internally a {@link HashBiMap} with keys of {@link ResourceLocation}s.
 * Note that this is NOT a part of Minecraft registry system.
 */
public class NFURegistry<T> implements DirectedGraphNode<NFURegistry<?>>
{
    public static final LimitedMutable<Boolean> COMMON_SETUP_DONE = new LimitedMutable<>(false, 1);
    public static final LimitedMutable<Boolean> CLIENT_SETUP_DONE = new LimitedMutable<>(false, 1);
    public static final LimitedMutable<Boolean> SERVER_SETUP_DONE = new LimitedMutable<>(false, 1);

    /** All declared registries. */
    private static final HashBiMap<ResourceLocation, NFURegistry<?>> REGISTRIES = HashBiMap.create();
    private final HashMap<ResourceLocation, Entry<? extends T>> table = new HashMap<>();
    private boolean shouldGenerateOnSetup = false;
    private int generateOnSetupPhase = 0;   // 0 = common setup: 1 = server setup; 2 = client setup
    private SetupPhase[] unavailableBefore = new SetupPhase[]{};
    private AvailableSide availableSide = AvailableSide.BOTH;
    /**
     * Indicates this registry should be loaded before the listed registries.
     */
    private final List<NFURegistry<?>> shouldLoadBefore = new ArrayList<>();

    /**
     * @param registryKey Key of this registry in the table of all registries.
     */
    public NFURegistry(ResourceLocation registryKey)
    {
        REGISTRIES.put(registryKey, this);
    }

    public static Map<ResourceLocation, NFURegistry<?>> allRegistries()
    {
        return REGISTRIES;
    }

    public static NFURegistry<?> registryByKey(ResourceLocation key)
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
     * Get all values.
     * <p>If a value appears twice in the registry, it will appear twice in this list. Null values will be removed.
     * <p>Note: this method will cause all entries to generate values. Take care of the timing if the values are valid!
     */
    public List<? extends T> values() {
        return table.values().stream().map(Entry::get).filter(Objects::nonNull).toList();
    }

    /**
     * Register an object from supplier.
     * @return An {@code Accessor} for getting the object, so that you can assign it to a
     * static field. Its usage is similar to {@link RegistryObject}.
     * <p>It's recommended to use {@link NFURegistryEntryCollection} instead (just like using {@link DeferredRegister}).
     * Directly registering may cause issues if the class in which you're registering objects is not loaded on setup phase.
     */
    public <U extends T> Accessor<U> register(ResourceLocation key, Supplier<U> supplier)
    {
        if (this.containsKey(key)) throw DuplicateRegistryEntryException.registeredTwice(key.toString());
        Entry<U> entry = new Entry<>(this, supplier, key);
        this.table.put(key, entry);
        return new Accessor<>(entry);
    }

    /**
     * Register an object from supplier if it's not present.
     * @return An {@code Optional<Accessor>} if registered. {@code Optional#empty()} if the entry exists.
     */
    public <U extends T> Optional<Accessor<U>> registerIfAbsent(ResourceLocation key, Supplier<U> supplier)
    {
        if (this.containsKey(key)) return Optional.empty();
        Entry<U> entry = new Entry<>(this, supplier, key);
        this.table.put(key, entry);
        return Optional.of(new Accessor<>(entry));
    }

    /**
     * Only for {@link NFURegistryEntryCollection}.
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
     * Generate all values that haven't generated. It doesn't impact values already generated.
     */
    public void generateAllValues()
    {
        this.table.keySet().forEach(this::getValue);
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
     * Called only in {@link NFUSetupEventHandlers#generateRegistries}.
     */
    public boolean shouldGenerateOnSetup()
    {
        return this.shouldGenerateOnSetup;
    }

    /**
     * Labels that this registry's all values should be generated on the common setup phase.
     * Registries with this label will generate values on {@link FMLCommonSetupEvent}.
     * @return {@code this}.
     */
    public NFURegistry<T> setShouldGenerateOnCommonSetup()
    {
        this.shouldGenerateOnSetup = true;
        return this;
    }

    /**
     * Labels that this registry's all values should be generated on server setup phase (e.g. requiring data reading).
     * Registries with this label will generate values on {@link ServerAboutToStartEvent}.
     * <p>Note: Use this only for server-side registries. Values will not generate on client.
     * @return {@code this}.
     */
    public NFURegistry<T> setShouldGenerateOnServerSetup()
    {
        this.shouldGenerateOnSetup = true;
        this.generateOnSetupPhase = 1;
        return this;
    }

    /**
     * Labels that this registry's all values should be generated on server setup phase (e.g. requiring data reading).
     * Registries with this label will generate values on {@link ServerStartingEvent}.
     * @return {@code this}.
     */
    public NFURegistry<T> setShouldGenerateOnClientSetup()
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

    /**
     * Check if this registry is unavailable before a given phase. If {@link Accessor#get()} is called before this phase,
     * it will always return {@code null}.
     */
    public boolean isUnavailableBefore(SetupPhase phase) {
        return Arrays.stream(unavailableBefore).toList().contains(phase);
    }

    /**
     * Set this registry is unavailable before given phase(s). If {@link Accessor#get()} is called before the set phase(s),
     * it will always return {@code null}.
     */
    public NFURegistry<T> setUnavailableBefore(SetupPhase... phases) {
        this.unavailableBefore = phases;
        return this;
    }

    /**
     * Check if the registry is called on the correct logical side.
     */
    public boolean isCorrectSide() {
        return this.availableSide.equals(AvailableSide.BOTH) ||
                (EffectiveSide.get().isClient() && this.availableSide.equals(AvailableSide.CLIENT)) ||
                (EffectiveSide.get().isServer() && this.availableSide.equals(AvailableSide.SERVER));
    }

    /**
     * Set this registry should be only available on a given logical side. If {@link Accessor#get()} is called on the
     * wrong side, it will always return {@code null}.
     */
    public NFURegistry<T> setSide(AvailableSide side) {
        this.availableSide = side;
        return this;
    }

    /**
     * Set that this registry should be loaded before the following registries.
     */
    public NFURegistry<T> setLoadBefore(NFURegistry<?>... registries) {
        for (NFURegistry<?> reg: registries) {
            this.shouldLoadBefore.add(reg);
            // Detect cycle
            List<NFURegistry<?>> cycle = this.getCycle();
            if (cycle != null) {
                this.shouldLoadBefore.remove(reg);
                StringBuilder cycleInfo = new StringBuilder(cycle.get(0).getKeyOfRegistry().toString());
                for (int i = 1; i < cycle.size(); ++i)
                    cycleInfo.append(" -> ").append(cycle.get(i).getKeyOfRegistry().toString());
                throw new IllegalArgumentException("NaUtilsRegistry loading order error: cyclic dependency detected.\n" +
                        "Cycle: " + cycleInfo);
            }
        }
        return this;
    }

    /**
     * Set that this registry should be loaded after the following registries.
     */
    public NFURegistry<T> setLoadAfter(NFURegistry<?>... registries) {
        for (NFURegistry<?> reg: registries) {
            reg.setLoadBefore(this);
        }
        return this;
    }

    public boolean shouldLoadBefore(NFURegistry<?> other) {
        return this.shouldLoadBefore.contains(other) && !other.shouldLoadBefore.contains(this);
    }

    public boolean shouldLoadAfter(NFURegistry<?> other) {
        return !this.shouldLoadBefore.contains(other) && other.shouldLoadBefore.contains(this);
    }

    /**
     * @return Registries that should be loaded after this registry.
     */
    @Override
    public Set<NFURegistry<?>> children() {
        return Set.copyOf(this.shouldLoadBefore);
    }

    static class Entry<T>
    {
        private final Supplier<T> supplier;
        private T cachedValue;
        private final NFURegistry<? super T> registry;
        private final ResourceLocation key;

        public Entry(@Nonnull NFURegistry<? super T> registry, @Nonnull Supplier<T> supplier, ResourceLocation key)
        {
            this.supplier = supplier;
            this.key = key;
            this.cachedValue = null;
            this.registry = registry;
        }

        /**
         * Get value from the supplier. Note that once the supplier output a valid value,
         * it won't rerun (i.e. the value won't change) until {@code regenerate} is called.
         */
        @Nullable
        public T get()
        {
            if (!registry.isCorrectSide())
                return null;
            if (registry.isUnavailableBefore(SetupPhase.COMMON_SETUP) && !NFURegistry.COMMON_SETUP_DONE.get())
                return null;
            if (registry.isUnavailableBefore(SetupPhase.CLIENT_SETUP) && !NFURegistry.CLIENT_SETUP_DONE.get() && EffectiveSide.get().isClient())
                return null;
            if (registry.isUnavailableBefore(SetupPhase.SERVER_SETUP) && !NFURegistry.SERVER_SETUP_DONE.get() && !EffectiveSide.get().isClient())
                return null;
            if (cachedValue == null) {
                try {
                    cachedValue = supplier.get();
                } catch (RuntimeException e)
                {
                    // If running supplier encountered error, don't crash but
                    // set the cache to null so that the supplier will rerun next time.
                    NFUDebugStatics.errorOnce(NFURegistry.Accessor.class,
                            "Registry entry getting value failed. Registry=\"" + this.registry.getKeyOfRegistry()
                    + "\", key=\"" + this.key + "\".");
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

        Accessor(Entry<T> entry) {
            this.entry = entry;
            this.validated = true;
        }



        static <U> Accessor<U> createInvalid(Entry<U> entry)
        {
            Accessor<U> res = new Accessor<>(entry);
            res.validated = false;
            return res;
        }

        @Override
        public T get()
        {
            if (!validated) return null;
            return entry.get();
        }

        Accessor<T> validate() {this.validated = true; return this;}
    }

    public static enum SetupPhase {
        COMMON_SETUP, CLIENT_SETUP, SERVER_SETUP;
    }

    public static enum AvailableSide {
        SERVER, CLIENT, BOTH
    }

    public static List<NFURegistry<?>> sortByLoadingOrder(Collection<NFURegistry<?>> raw) {
        return DirectedGraphNode.sortByOccurrenceOrder(raw);
    }

    /**
     * NYI
     * Indicates how it should handle sync. This record should exist on both sides no matter on which side
     * the registry is available.
     * @param shouldSync If true, it will sync the data to another side.
     * @param from Where the data is posted from.
     * @param encoder How the data should be written to buffer.
     * @param decoder How the data should be generated from buffer.
     */
    public static record SyncPolicy<T>(boolean shouldSync, LogicalSide from, BiConsumer<FriendlyByteBuf, T> encoder, Function<FriendlyByteBuf, T> decoder) {

        public static <T> SyncPolicy<T> noSync() {
            return new SyncPolicy<>(false, null, null, null);
        }

        public static <T> SyncPolicy<T> fromServer(@Nonnull BiConsumer<FriendlyByteBuf, T> encoder,
                                                   @Nonnull Function<FriendlyByteBuf, T> decoder) {
            return new SyncPolicy<>(true, LogicalSide.SERVER, encoder, decoder);
        }

        public static <T> SyncPolicy<T> fromServer(NFUDataSerializer<T> serializer) {
            return fromServer(serializer::write, serializer::read);
        }

        public static <T> SyncPolicy<T> fromClient(@Nonnull BiConsumer<FriendlyByteBuf, T> encoder,
                                                   @Nonnull Function<FriendlyByteBuf, T> decoder) {
            return new SyncPolicy<>(true, LogicalSide.CLIENT, encoder, decoder);
        }

        public static <T> SyncPolicy<T> fromClient(NFUDataSerializer<T> serializer) {
            return fromClient(serializer::write, serializer::read);
        }

    }

}
