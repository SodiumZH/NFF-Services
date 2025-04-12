package net.sodiumzh.nfu.container;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * A key-value pair for maps.
 * @deprecated Use {@link Tuple2} instead.
 */
@Deprecated
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

	@Override
	public boolean equals(Object other) {
		return other instanceof MapPair<?,?> mapPair && Objects.equals(this.getKey(), mapPair.getKey())
				&& Objects.equals(this.getValue(), mapPair.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getKey(), this.getValue());
	}
}
