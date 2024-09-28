package net.sodiumzh.nautils.containers;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A key-value pair for maps.
 * It's similar to {#link java.util.Map#Entry} but supporting null values.
 */
public class MapPair<K, V> implements Map.Entry<K, V> 
{
	@Nonnull
	protected K k;
	@Nullable
	protected V v;
	
	private MapPair(K key, V value)
	{
		if (key == null)
			throw new IllegalArgumentException("MapPair key cannot be null.");
		this.k = key;
		this.v = value;
	}
	
	@Deprecated
	public K getK()
	{
		return k;
	}
	
	@Deprecated
	public V getV()
	{
		return v;
	}
	
	public static <K, V> MapPair<K, V> of(@Nonnull K k, V v)
	{
		return new MapPair<K, V>(k, v);
	}

	@Override
	public K getKey() {
		return k;
	}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public V setValue(V value) {
		V old = v;
		v = value;
		return old;
	}
}
