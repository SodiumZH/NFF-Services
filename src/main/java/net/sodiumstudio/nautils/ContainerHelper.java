package net.sodiumstudio.nautils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sodiumstudio.nautils.containers.MapPair;

public class ContainerHelper
{
	// Remove all elements fulfilling a condition from a set
	public static <T> void removeFromSet(Set<T> set, Predicate<T> condition)
	{
		HashSet<T> toRemove = new HashSet<T>();
		for (T t: set)
		{
			if (condition.test(t))
				toRemove.add(t);
		}
		for (T t: toRemove)
		{
			set.remove(t);
		}
	}
	
	// Remove all elements with the key fulfilling a condition from a map
	public static <T, U> void removeFromMapKey(Map<T, U> map, Predicate<T> keyCondition)
	{
		HashSet<T> toRemove = new HashSet<T>();
		for (T t: map.keySet())
		{
			if (keyCondition.test(t))
				toRemove.add(t);
		}
		for (T t: toRemove)
		{
			map.remove(t);
		}
	}
	
	// Remove all elements with the value fulfilling a condition from a map
	public static <T, U> void removeFromMapValue(Map<T, U> map, Predicate<U> valueCondition)
	{
		HashSet<T> toRemove = new HashSet<T>();
		for (T t: map.keySet())
		{
			if (valueCondition.test(map.get(t)))
				toRemove.add(t);
		}
		for (T t: toRemove)
		{
			map.remove(t);
		}
	}
	
	// Pick an element fulfilling the condition from a set.
	// If there are multiple, it will only pick one.
	public static <T> T pickSetElement(Set<T> set, Predicate<T> condition)
	{
		for (T t: set)
		{
			if (condition.test(t))
				return t;
		}
		return null;
	}
	
	// Pick all elements fulfilling the condition from a set.
	public static <T> HashSet<T> pickSetElements(Set<T> set, Predicate<T> condition)
	{
		HashSet<T> out = new HashSet<T>();
		for (T t: set)
		{
			if (condition.test(t))
				out.add(t);
		}
		return out;
	}
	
	/**
	 * Transform an iterable to list (array list).
	 * @param assumedSize Size assumption for list initial capacity.
	 */
	public static <T> List<T> iterableToList(Iterable<T> iterable, int assumedSize)
	{
		List<T> list = new ArrayList<T>(assumedSize * 2);
		for (T obj: iterable)
		{
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * Transform an iterable to list (array list).
	 * @param assumedSize Size assumption for list initial capacity.
	 */
	public static <T> List<T> iterableToList(Iterable<T> iterable)
	{
		List<T> list = new ArrayList<T>();
		for (T obj: iterable)
		{
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * Get a mutable list (ArrayList) of given values.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> listOf(T... values)
	{
		List<T> list = new ArrayList<T>(values.length * 2);
		for (T t: values)
		{
			list.add(t);
		}
		return list;
	}
	
	/**
	 * Get a mutable set (HashSet) of given values.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> setOf(T... values)
	{
		Set<T> set = new HashSet<T>();
		for (T t: values)
		{
			set.add(t);
		}
		return set;
	}
	
	/**
	 * Get a mutable map (HashMap) of given values.
	 */
	public static <T, U> Map<T, U> mapOf(List<T> keyList, List<U> valueList)
	{
		HashMap<T, U> map = new HashMap<T, U>();
		if (keyList.size() != valueList.size())
			throw new IllegalArgumentException("keyList and valueList length not same.");
		for (int i = 0; i < keyList.size(); ++i)
		{
			map.put(keyList.get(i), valueList.get(i));
		}
		return map;
	}
	
	/**
	 * Get a mutable map (HashMap) of given values.
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> Map<T, U> mapOf(MapPair<T, U>... entries)
	{
		HashMap<T, U> map = new HashMap<T, U>();
		for (MapPair<T, U> entry: entries)
		{
			map.put(entry.getK(), entry.getV());
		}
		return map;
	}
	
	
	public static <K, V, k, v> Map<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast, boolean keyNonnull, boolean valueNonnull)
	{
		Map<k, v> newMap = new HashMap<k, v>();
		for (K oldKey: map.keySet())
		{
			k newKey = keyCast.apply(oldKey);
			v newVal = valueCast.apply(map.get(oldKey));
			if ((newKey != null || !keyNonnull) && (newVal != null || !valueNonnull))
				newMap.put(keyCast.apply(oldKey), valueCast.apply(null));
		}
		return newMap;
	}
	
	public static <K, V, k, v> Map<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast, boolean keyNonnull)
	{
		return castMap(map, keyCast, valueCast, keyNonnull, false);
	}
			
	public static <K, V, k, v> Map<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast)
	{
		return castMap(map, keyCast, valueCast, true);
	}
}
