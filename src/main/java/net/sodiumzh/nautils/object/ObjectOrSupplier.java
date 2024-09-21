package net.sodiumzh.nautils.object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;

public class ObjectOrSupplier<T> implements Supplier<T>
{
	private final T object;
	private final Supplier<T> supplier;
	
	private ObjectOrSupplier(T object)
	{
		this.object = object;
		this.supplier = null;
	}
	
	private ObjectOrSupplier(Supplier<T> supplier)
	{
		this.supplier = supplier;
		this.object = null;
	}
	
	public static <T> ObjectOrSupplier<T> of(T object)
	{
		return new ObjectOrSupplier<>(object);
	}
	
	public static <T> ObjectOrSupplier<T> of(Supplier<T> supplier)
	{
		return new ObjectOrSupplier<>(supplier);
	}

	public boolean isSupplier()
	{
		return this.supplier != null;
	}
	
	@Override
	public T get()
	{
		if (this.supplier != null)
			return supplier.get();
		else return object;
	}
	
	// Utils
	
	
	/**
	 * Cast map keys from object to {@link ObjectOrSupplier}.
	 */
	public static <K, V> HashMap<ObjectOrSupplier<K>, V> fromObjectKeyMap(Map<K, V> in)
	{
		return NaUtilsContainerStatics.castMap(in, k -> ObjectOrSupplier.of(k), v -> v, true);
	}
	
	/**
	 * Cast map values from object to {@link ObjectOrSupplier}.
	 */
	public static <K, V> HashMap<K, ObjectOrSupplier<V>> fromObjectValueMap(Map<K, V> in)
	{
		return NaUtilsContainerStatics.castMap(in, k -> k, v -> ObjectOrSupplier.of(v), true); 
	}
	
	/**
	 * Cast set elements from object to {@link ObjectOrSupplier}.
	 */
	public static <T> HashSet<ObjectOrSupplier<T>> fromObjectSet(Set<T> in)
	{
		HashSet<ObjectOrSupplier<T>> out = new HashSet<>();
		in.forEach(elem -> out.add(ObjectOrSupplier.of(elem)));
		return out;
	}
	
}
