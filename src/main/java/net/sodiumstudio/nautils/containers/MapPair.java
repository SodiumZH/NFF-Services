package net.sodiumstudio.nautils.containers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A key-value pair for maps.
 * It's similar to {#link java.util.Map#Entry} but supporting null values.
 */
public class MapPair <K, V>
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
	
	public K getK()
	{
		return k;
	}
	
	public V getV()
	{
		return v;
	}
	
	public static <K, V> MapPair<K, V> of(K k, V v)
	{
		return new MapPair<K, V>(k, v);
	}
}
