package net.sodiumstudio.nautils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sodiumstudio.nautils.containers.MapPair;

/**
 * Utility static methods for containers (List, Set, Map, etc).
 */
public class ContainerHelper
{
	
	protected static Random rnd = new Random();
	
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
	public static <T> ArrayList<T> iterableToList(Iterable<T> iterable, int assumedSize)
	{
		ArrayList<T> list = new ArrayList<T>(assumedSize * 2);
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
	public static <T> ArrayList<T> iterableToList(Iterable<T> iterable)
	{
		ArrayList<T> list = new ArrayList<T>();
		for (T obj: iterable)
		{
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * Transform an iterable to set (HashSet).
	 * @param assumedSize Size assumption for list initial capacity.
	 */
	public static <T> HashSet<T> iterableToSet(Iterable<T> iterable)
	{
		HashSet<T> set = new HashSet<T>();
		for (T obj: iterable)
		{
			set.add(obj);
		}
		return set;
	}
	
	/**
	 * Get a mutable list (ArrayList) of given values.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> listOf(T... values)
	{
		ArrayList<T> list = new ArrayList<T>(values.length * 2);
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
	public static <T> HashSet<T> setOf(T... values)
	{
		HashSet<T> set = new HashSet<T>();
		for (T t: values)
		{
			set.add(t);
		}
		return set;
	}
	
	/**
	 * Get a mutable map (HashMap) of given values.
	 */
	public static <T, U> HashMap<T, U> mapOf(List<T> keyList, List<U> valueList)
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
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T, U> HashMap<T, U> mapOf(MapPair<T, U>... entries)
	{
		HashMap<T, U> map = new HashMap<T, U>();
		for (MapPair<T, U> entry: entries)
		{
			map.put(entry.getK(), entry.getV());
		}
		return map;
	}
	
	/**
	 * Cast all element pairs from a map to another.
	 * @param <K> Key type of old map.
	 * @param <V> Value type of old map.
	 * @param <k> Key type of new map.
	 * @param <v> Value type of new map.
	 * @param map Old map.
	 * @param keyCast Function casting map keys.
	 * @param valueCast Function casting map values.
	 * @param keyNonnull If true, the new map will ignore a pair if its key is null.
	 * @param valueNonnull If true, the new map will ignore a pair if its value is null.
	 * @return Casted new map.
	 */
	public static <K, V, k, v> HashMap<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast, boolean keyNonnull, boolean valueNonnull)
	{
		HashMap<k, v> newMap = new HashMap<k, v>();
		for (K oldKey: map.keySet())
		{
			k newKey = keyCast.apply(oldKey);
			v newVal = valueCast.apply(map.get(oldKey));
			if ((newKey != null || !keyNonnull) && (newVal != null || !valueNonnull))
				newMap.put(newKey, newVal);
		}
		return newMap;
	}
	
	public static <K, V, k, v> HashMap<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast, boolean keyNonnull)
	{
		return castMap(map, keyCast, valueCast, keyNonnull, false);
	}
			
	public static <K, V, k, v> HashMap<k, v> castMap(Map<K, V> map, Function<K, k> keyCast, Function<V, v> valueCast)
	{
		return castMap(map, keyCast, valueCast, true);
	}
	
	/**
	 * Randomly pick an element in a collection
	 * For {@link List}, use {@code randomPick} instead since it's faster for large collections.
	 */
	public static <T> T randomPickCollection(Collection<T> collection)
	{
		int r = rnd.nextInt(collection.size());
		int i = 0;
		for (T t: collection)
		{
			if (i == r)
				return t;
			else ++i;
		}
		throw new RuntimeException();
	}
	
	/**
	 * Randomly pick a key-value pair in a map
	 */
	public static <K, V> MapPair<K, V> randomPick(Map<K, V> map)
	{
		K k = randomPickCollection(map.keySet());
		return MapPair.of(k, map.get(k));
	}
	
	/**
	 * Randomly pick an element in a list
	 */
	public static <T> T randomPick(List<T> list)
	{
		return list.get(rnd.nextInt(0, list.size()));
	}
	
	/**
	 * Gather elements fulfilling certain condition, transform with a function and collect into a list
	 */
	public static <T, U> ArrayList<U> collectAndTransform(Collection<T> from, Predicate<T> condition, Function<T, U> transformation)
	{
		ArrayList<U> list = new ArrayList<>();
		from.stream().filter(condition).forEach((T t) -> list.add(transformation.apply(t)));
		return list;
	}
	
	/**
	 * Gather elements fulfilling certain condition, transform with a function and collect into a list
	 * <p> Note: use this only for non-collection iterables because it's slower. For collections, use {@code collectAndTransform} instead.
	 */
	public static <T, U> ArrayList<U> collectIterableAndTransform(Iterable<T> from, Predicate<T> condition, Function<T, U> transformation)
	{
		ArrayList<U> list = new ArrayList<>();
		for (T t: from)
		{
			if (condition.test(t))
			{
				list.add(transformation.apply(t));
			}
		}
		return list;
	}
	
	/**
	 * Cast a list element-wise to a subclass. If cast failed, the element will be ignored.
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> ArrayList<U> castListType(List<T> list, Class<U> castToClass)
	{
		ArrayList<U> out = new ArrayList<>();
		list.forEach(t -> {
			if (castToClass.isAssignableFrom(t.getClass()))
			{
				out.add((U)t);
			}
		});
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public static <T, U> ArrayList<U> castListTypeUnchecked(List<T> list, boolean suppressException)
	{
		ArrayList<U> out = new ArrayList<>();
		list.forEach(t -> {
			try {
				out.add((U)t);
			} 
			catch (ClassCastException e) {
				if (!suppressException)
					e.printStackTrace();
			}
		});
		return out;
	}
	
	public static <T, U> ArrayList<U> castListTypeUnchecked(List<T> list)
	{
		return castListTypeUnchecked(list, false);
	}
}
