package net.sodiumzh.nautils.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ObjectOrKey<T> implements Supplier<T>
{
	private final T object;
	private final ResourceLocation key;
	private final IForgeRegistry<T> registry;
	
	public ObjectOrKey(T object)
	{
		this.object = object;
		this.key = null;
		this.registry = null;
	}
	
	public ObjectOrKey(ResourceLocation key, IForgeRegistry<T> registry)
	{
		this.object = null;
		this.key = key;
		this.registry = registry;
	}

	public ObjectOrKey(String key, IForgeRegistry<T> registry)
	{
		this(new ResourceLocation(key), registry);
	}
	
	public ObjectOrKey(String namespace, String key, IForgeRegistry<T> registry)
	{
		this(new ResourceLocation(namespace, key), registry);
	}
	
	public boolean isKey()
	{
		return this.key != null;
	}
	
	@Nullable
	@Override
	public T get()
	{
		if (this.key != null)
			return registry.getValue(key);
		else return object;
	}
	
	// Utilities
	
	/**
	 * Get values from {@code List} of {@code ObjectOrKey}. Null objects will be ignored.
	 */
	public static <T, U extends ObjectOrKey<? extends T>> ArrayList<T> getNonnull(List<U> list)
	{
		ArrayList<T> res = new ArrayList<>(list.size());
		for (var obj: list)
		{
			T t = obj.get();
			if (t != null)
				res.add(t);
		}
		return res;
	}
	
	/**
	 * Get values from {@code Set} of {@code ObjectOrKey}. Null objects will be ignored.
	 */
	public static <T, U extends ObjectOrKey<T>> HashSet<T> getNonnull(Set<U> set)
	{
		HashSet<T> res = new HashSet<>();
		for (var obj: set)
		{
			T t = obj.get();
			if (t != null)
				res.add(t);
		}
		return res;
	}
	
	/**
	 * Get values from {@code Map} with {@code ObjectOrKey} values. Null objects will be ignored.
	 */
	public static <K, V, U extends ObjectOrKey<V>> HashMap<K, V> getNonnull(Map<K, U> map)
	{
		HashMap<K, V> res = new HashMap<>();
		for (var key: map.keySet())
		{
			V v = map.get(key).get();
			if (v != null)
			{
				res.put(key, v);
			}
		}
		return res;
	}
	
	/**
	 * Get values from {@code Map} with {@code ObjectOrKey} keys. Null objects will be ignored.
	 */
	public static <K, V, U extends ObjectOrKey<K>> HashMap<K, V> getNonnullKeys(Map<U, V> map)
	{
		HashMap<K, V> res = new HashMap<>();
		for (var key: map.keySet())
		{
			K k = key.get();
			if (k != null)
			{
				res.put(k, map.get(key));
			}
		}
		return res;
	}
	
	/**
	 * Get values from {@code List} of {@code ObjectOrKey}. Null objects will still be added.
	 */
	public static <T, U extends ObjectOrKey<? extends T>> ArrayList<T> getNullable(List<U> list)
	{
		ArrayList<T> res = new ArrayList<>(list.size());
		for (var obj: list)
		{
			res.add(obj.get());
		}
		return res;
	}
	
	/**
	 * Get values from {@code Map} with {@code ObjectOrKey} values. Null objects will still be added.
	 */
	public static <K, T, U extends ObjectOrKey<T>> HashMap<K, T> getNullable(Map<K, U> map)
	{
		HashMap<K, T> res = new HashMap<>();
		for (var key: map.keySet())
		{
			T t = map.get(key).get();
			res.put(key, t);
		}
		return res;
	}
	
}
