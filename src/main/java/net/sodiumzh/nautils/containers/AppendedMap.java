package net.sodiumzh.nautils.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mojang.logging.LogUtils;

/**
 * An {@code AppendedMap} is a map containing two parts. One is a reference of an external map, the other is internal.
 * <p>It's intended to enable appending an existing map without actually modifying it.
 * <p>For example, when you have map1 and create an {@AppendedMap} map2 with it. Then when you modify map1 outside, map2 will
 * also change as map1 is only a reference in map2; but when adding new elements to map2, map1 won't be modified as the new entry
 * is added in the internal part, not in external map1.
 */
public class AppendedMap<K, V> implements Map<K, V>
{
	private final Map<K, V> externalPart;
	private final HashMap<K, V> internalPart = new HashMap<>();
	private boolean allowsModifyingExternalPart = false;
	
	public AppendedMap(Map<K, V> from)
	{
		this.externalPart = from;
	}
	
	public boolean allowsModifyingExternalPart()
	{
		return this.allowsModifyingExternalPart;
	}
	
	public AppendedMap<K, V> setAllowsModifyingExternalPart(boolean value)
	{
		this.allowsModifyingExternalPart = value;
		return this;
	}
	
	@Override
	public int size() {
		return this.externalPart.size() + this.internalPart.size();
	}

	@Override
	public boolean isEmpty() {
		return this.externalPart.isEmpty() && this.internalPart.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.externalPart.containsKey(key) || this.internalPart.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.externalPart.containsValue(value) || this.internalPart.containsValue(value);
	}

	@Override
	public V get(Object key) {
		V res = this.externalPart.get(key);
		if (res == null)
			res = this.internalPart.get(key);
		return res;
	}

	@Override
	public V put(K key, V value) {
		if (this.externalPart.containsKey(key))
		{
			if (this.allowsModifyingExternalPart())
			{
				return this.externalPart.put(key, value);
			}
			else
			{
				LogUtils.getLogger().error("NaUtils - AppendedMap: Attempting to overwrite external part entry. "
						+ "Skipped. To actually override, call setAllowsModifyingExternalPart(true) first.");
				return this.externalPart.get(key);
			}
		}
		else return this.internalPart.put(key, value);
	}

	@Override
	public V remove(Object key) {
		if (this.externalPart.containsKey(key))
		{
			if (this.allowsModifyingExternalPart())
			{
				return this.externalPart.remove(key);
			}
			else
			{
				LogUtils.getLogger().error("NaUtils - AppendedMap: Attempting to remove external part entry. "
						+ "Skipped and returned the corresponding value. To actually override, "
						+ "call setAllowsModifyingExternalPart(true) first.");
				return this.externalPart.get(key);
			}
		}
		else return this.internalPart.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (var entry: m.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		if (this.allowsModifyingExternalPart())
			this.externalPart.clear();
		this.internalPart.clear();
		
	}

	@Override
	public Set<K> keySet() {
		Set<K> res = this.internalPart.keySet();
		res.addAll(this.externalPart.keySet());
		return res;
	}

	@Override
	public Collection<V> values() {
		Collection<V> res = this.internalPart.values();
		res.addAll(this.externalPart.values());
		return res;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> res = this.internalPart.entrySet();
		res.addAll(this.externalPart.entrySet());
		return res;
	}

	@Override
	public String toString()
	{
		return "AppendedMap: EXTERNAL = \n" + this.externalPart.toString() + ",\nINTERNAL = \n" + this.internalPart.toString();
	}
	
}
