package net.sodiumzh.nautils.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mojang.logging.LogUtils;

/**
 * An immutable map implementation that can be directly created from a generic map or an element collection.
 * It only supports getting, but not putting or removing.
 * <p>Generally it's used to prevent accident unexpected/invalid modification of a returned map.
 */
public class NaUtilsImmutableMap<K, V> implements Map<K, V>
{

	private final Map<K, V> internal;
	private final boolean suppressException;
	private final boolean silent;
	
	/**
	 * Make from entry iterable.
	 * @param suppressException If true, modification attempts will just do nothing and print an error information but won't trigger exceptions.
	 * @param silent If true, modification attempts will do nothing and DON'T OUTPUT ANYTHING. Not recommended. (Requires suppressException)
	 */
	public NaUtilsImmutableMap(Iterable<Entry<K, V>> from, boolean suppressException, boolean silent)
	{
		this.internal = new HashMap<K, V>();
		for (Entry<K, V> entry: from)
		{
			if (internal.containsKey(entry.getKey()))
				internal.put(entry.getKey(), entry.getValue());
			else throw new IllegalArgumentException("Duplicated entry keys.");
		}
		this.suppressException = suppressException;
		this.silent = silent;
	}
	
	/**
	 * Make from entry iterable.
	 * @param suppressException If true, modification attempts will just do nothing and print an error information but won't trigger exceptions.
	 */
	public NaUtilsImmutableMap(Iterable<Entry<K, V>> from, boolean suppressException)
	{
		this(from, suppressException, false);
	}
	
	/**
	 * Make from entry iterable. Don't suppress exceptions.
	 */
	public NaUtilsImmutableMap(Iterable<Entry<K, V>> from)
	{
		this(from, false, false);
	}
	
	/**
	 * Make from a generic map.
	 * @param suppressException If true, modification attempts will just do nothing and print an error information but won't trigger exceptions.
	 * @param silent If true, modification attempts will do nothing and DON'T OUTPUT ANYTHING. Not recommended. (Requires suppressException)
	 */
	public NaUtilsImmutableMap(Map<K, V> from, boolean suppressException, boolean silent)
	{
		this.internal = from;
		this.suppressException = suppressException;
		this.silent = silent;
	}
	
	/**
	 * Make from a generic map.
	 * @param suppressException If true, modification attempts will just do nothing and print an error information but won't trigger exceptions.
	 */
	public NaUtilsImmutableMap(Map<K, V> from, boolean suppressException)
	{
		this(from, suppressException, false);
	}
	
	/**
	 * Copy from a generic map. Don't suppress exceptions.
	 */
	public NaUtilsImmutableMap(Map<K, V> from)
	{
		this(from, false, false);
	}

	
	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internal.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return internal.get(key);
	}

	@Override
	public V put(K key, V value) {
		if (suppressException)
		{
			if (!silent)
			{
				LogUtils.getLogger().error("NaUtilsImmutableMap: illegal modification (put).");
			}
			return value;
		}
		throw new UnsupportedOperationException("NaUtilsImmutableMap: illegal modification (put).");
	}

	@Override
	public V remove(Object key) {
		if (suppressException)
		{
			if (!silent)
			{
				LogUtils.getLogger().error("NaUtilsImmutableMap: illegal modification (remove).");
			}
			return internal.get(key);
		}
		throw new UnsupportedOperationException("NaUtilsImmutableMap: illegal modification (remove).");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (suppressException)
		{
			if (!silent)
			{
				LogUtils.getLogger().error("NaUtilsImmutableMap: illegal modification (putAll).");
			}
			return;
		}
		throw new UnsupportedOperationException("NaUtilsImmutableMap: illegal modification (putAll).");
	}

	@Override
	public void clear() {
		if (suppressException)
		{
			if (!silent)
			{
				LogUtils.getLogger().error("NaUtilsImmutableMap: illegal modification (clear).");
			}
			return;
		}
		throw new UnsupportedOperationException("NaUtilsImmutableMap: illegal modification (clear).");
	}

	@Override
	public Set<K> keySet() {
		return internal.keySet();
	}

	@Override
	public Collection<V> values() {
		return internal.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return internal.entrySet();
	}

}
