package net.sodiumzh.nfu.container;

import com.google.common.collect.*;
import net.sodiumzh.nfu.object.DirectedGraphNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@code LinkableMultimap} is a {@link SetMultimap} with attachments to other multimaps (either as {@link Multimap}s
 * or {@link Map}s with {@link Collection} values). Query of the {@code LinkableMultimap} will
 * return the union of the value sets of this and all attached maps. Modification will always operate this multimap but
 * never attachments.
 * <p>Note: it's much more costly than normal {@link SetMultimap}s and {@link Map}s, as query operation will cause
 * queries of all attachments and possibly, for {@code LinkableMultimap} attachments, recursive queries of their attachments.
 */
public class LinkableMultimap<K, V> implements SetMultimap<K, V>, DirectedGraphNode<LinkableMultimap<K, V>> {

    private final SetMultimap<K, V> original = HashMultimap.create();
    private final Set<Multimap<K, V>> attachedMultimaps = new HashSet<>();
    private final Set<Map<K, Collection<V>>> attachedSetMaps = new HashSet<>();

    public HashMultimap<K, V> copyAsHash() {
        HashMultimap<K, V> res = HashMultimap.create();
        res.putAll(original);
        attachedMultimaps.forEach(res::putAll);
        attachedSetMaps.forEach(m -> m.keySet().forEach(k -> res.putAll(k, m.get(k))));
        return res;
    }

    /**
     * Get an immutable {@link SetMultimap} containing all unique entries of the original
     * and linked maps.
     */
    public ImmutableSetMultimap<K, V> copyAsImmutable() {
        return ImmutableSetMultimap.copyOf(this.copyAsHash());
    }

    /**
     * Attach an external {@link Multimap}. The linked map's entries will be present in this
     * map, but cannot be modified here. External modification of the linked map will impact
     * this map as well.
     * <p>Note: it will detect cyclic reference chain and throw exception if detected.
     * @throws IllegalStateException If this multimap already has a cyclic reference chain.
     * @throws IllegalArgumentException If the attachment will cause a cyclic reference chain.
     */
    @SuppressWarnings("unchecked")
    public void attach(Multimap<K, ? extends V> multimap) {
        if (this.getCycle() != null)
            throw new IllegalStateException("LinkableMultimap: cyclic reference detected.");
        this.attachedMultimaps.add((Multimap<K, V>) multimap);
        if (multimap instanceof LinkableMultimap<?, ?> && this.getCycle() != null) {
            this.attachedMultimaps.remove(multimap);
            throw new IllegalArgumentException("LinkableMultimap#attach: attachment caused cyclic reference.");
        }
    }

    /**
     * Attach an external {@link Map} of the value collections.
     * The linked map's entries will be present in this
     * map, but cannot be modified here. External modification
     * of the linked map will impact this map as well.
     */
    @SuppressWarnings("unchecked")
    public void attach(Map<K, ? extends Collection<? extends V>> multimap) {
        this.attachedSetMaps.add((Map<K, Collection<V>>) multimap);
    }

    /**
     * Detect if there is any cyclic reference chains, and detach attachments causing a cyclic reference chain.
     */
    public void resolveCyclicReferenceChains() {
        List<LinkableMultimap<K, V>> cyclic = this.getCycle();
        while (cyclic != null) {
            this.attachedMultimaps.remove(cyclic.get(1));
            cyclic = this.getCycle();
        }
    }

    /**
     * Remove all attached external maps. This operation will not impact the original part.
     * @return Entries removed by detachment. If an entry is still present in the original part,
     * it will not be included.
     */
    public ImmutableSetMultimap<K, V> detachAll() {
        HashMultimap<K, V> res = this.copyAsHash();
        attachedMultimaps.clear();
        attachedSetMaps.clear();
        this.copyAsHash().entries().forEach(entry -> res.remove(entry.getKey(), entry.getValue()));
        return ImmutableSetMultimap.copyOf(res);
    }

    @Nonnull
    @Override
    public Set<V> get(K key) {
        return this.copyAsImmutable().get(key);
    }

    /**
     * Note: the output is immutable.
     */
    @Nonnull
    @Override
    public Set<K> keySet() {
        return this.copyAsImmutable().keySet();
    }

    /**
     * Note: the output is immutable.
     */
    @Nonnull
    @Override
    public Multiset<K> keys() {
        return this.copyAsImmutable().keys();
    }

    /**
     * Note: the output is immutable.
     */
    @Nonnull
    @Override
    public Collection<V> values() {
        return this.copyAsImmutable().values();
    }

    @Nonnull
    @Override
    public Set<V> removeAll(@Nullable Object key) {
        return this.original.removeAll(key);
    }

    /**
     * Remove ALL values and detach all external maps. To prevent detaching,
     * use {@code clearOriginal} instead.
     */
    @Override
    public void clear() {
        this.original.clear();
        this.attachedMultimaps.clear();
        this.attachedSetMaps.clear();
    }

    public ImmutableSetMultimap<K, V> clearOriginal() {
        HashMultimap<K, V> res = this.copyAsHash();
        this.original.clear();
        this.copyAsHash().entries().forEach(entry -> res.remove(entry.getKey(), entry.getValue()));
        return ImmutableSetMultimap.copyOf(res);
    }

    @Override
    public int size() {
        return this.copyAsImmutable().size();
    }

    @Override
    public boolean isEmpty() {
        return this.copyAsImmutable().isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.copyAsImmutable().containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return this.copyAsImmutable().containsValue(value);
    }

    @Override
    public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
        return this.copyAsImmutable().containsEntry(key, value);
    }

    @Override
    public boolean put(K key, V value) {
        return this.original.put(key, value);
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        int oldSize = this.copyAsImmutable().entries().size();
        this.original.remove(key, value);
        return this.copyAsImmutable().entries().size() != oldSize;
    }

    @Override
    public boolean putAll(K key, @Nonnull Iterable<? extends V> values) {
        int oldSize = this.copyAsImmutable().entries().size();
        this.original.putAll(key, values);
        return this.copyAsImmutable().entries().size() != oldSize;
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends K, ? extends V> multimap) {
        int oldSize = this.copyAsImmutable().entries().size();
        this.original.putAll(multimap);
        return this.copyAsImmutable().entries().size() != oldSize;
    }

    public boolean putAll(@Nonnull Map<? extends K, ? extends Collection<? extends V>> map) {
        int oldSize = this.copyAsImmutable().entries().size();
        for (var entry: map.entrySet()) {
            this.original.putAll(entry.getKey(), entry.getValue());
        }
        return this.copyAsImmutable().entries().size() != oldSize;
    }

    @Nonnull
    @Override
    public Set<V> replaceValues(K key, @Nonnull Iterable<? extends V> values) {
        return this.original.replaceValues(key, values);
    }

    @Nonnull
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return this.copyAsImmutable().entries();
    }

    @Nonnull
    @Override
    public Map<K, Collection<V>> asMap() {
        return copyAsImmutable().asMap();
    }

    @Override
    public Set<LinkableMultimap<K, V>> children() {
        return this.attachedMultimaps.stream().filter(multimap -> multimap instanceof LinkableMultimap<K, V>)
                .map(multimap -> (LinkableMultimap<K, V>) multimap).collect(Collectors.toSet());
    }
}
