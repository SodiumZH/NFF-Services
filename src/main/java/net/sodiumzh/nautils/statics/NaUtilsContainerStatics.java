package net.sodiumzh.nautils.statics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.sodiumzh.nautils.containers.MapPair;

/**
 * Utility static methods for containers (List, Set, Map, etc).
 */
public class NaUtilsContainerStatics
{
	
	protected static Random rnd = new Random();
	
	/**
	 * Remove all elements fulfilling a condition from a set
	 * @deprecated Use {@code Set#removeIf(Predicate)} instead.
	 */
	@Deprecated
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
	
	/** 
	 * Remove all elements with the key fulfilling a condition from a map.
	 */
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
	
	/** 
	 * Pick an element fulfilling the condition from a set.  If there are multiple, it will randomly pick one.
	 */
	public static <T> T pickSetElement(Set<T> set, Predicate<T> condition)
	{
		for (T t: set)
		{
			if (condition.test(t))
				return t;
		}
		return null;
	}
	
	/** Pick all elements fulfilling the condition from a set. */
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
	
	/** Pick all keys of which values satisfying the condition from a map. */
	public static <K, V> HashSet<K> pickMapKeys(Map<K, V> map, Predicate<V> valueCondition)
	{
		HashSet<K> out = new HashSet<>();
		for (K key: map.keySet())
		{
			if (valueCondition.test(map.get(key)))
				out.add(key);
		}
		return out;
	}
	
	/**
	 * Transform an iterable to list (ArrayList).
	 * @param assumedSize Size assumption for list initial capacity.
	 */
	public static <T> ArrayList<T> iterableToList(Iterable<T> iterable, int assumedSize)
	{
		if (iterable instanceof ArrayList<T> res)
			return res;
		ArrayList<T> list = new ArrayList<T>(assumedSize * 2);
		for (T obj: iterable)
		{
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * Transform an iterable to list (ArrayList).
	 */
	public static <T> ArrayList<T> iterableToList(Iterable<T> iterable)
	{
		if (iterable instanceof ArrayList<T> res)
			return res;
		ArrayList<T> list = new ArrayList<T>();
		for (T obj: iterable)
		{
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * Transform an iterable to set (HashSet).
	 */
	public static <T> HashSet<T> iterableToSet(Iterable<T> iterable)
	{
		if (iterable instanceof HashSet<T> res)
			return res;
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
	 * @param <K1> Key type of old map.
	 * @param <V1> Value type of old map.
	 * @param <K2> Key type of new map.
	 * @param <V2> Value type of new map.
	 * @param map Old map.
	 * @param keyCast Function casting map keys.
	 * @param valueCast Function casting map values.
	 * @param keyNonnull If true, the new map will ignore a pair if its key is null.
	 * @param valueNonnull If true, the new map will ignore a pair if its value is null.
	 * @return Casted new map.
	 */
	public static <K1, V1, K2, V2> HashMap<K2, V2> castMap(Map<K1, V1> map, Function<K1, K2> keyCast, Function<V1, V2> valueCast, 
			boolean keyNonnull, boolean valueNonnull)
	{
		HashMap<K2, V2> newMap = new HashMap<K2, V2>();
		for (K1 oldKey: map.keySet())
		{
			K2 newKey = keyCast.apply(oldKey);
			V2 newVal = valueCast.apply(map.get(oldKey));
			if ((newKey != null || !keyNonnull) && (newVal != null || !valueNonnull))
				newMap.put(newKey, newVal);
		}
		return newMap;
	}
	
	public static <K1, V1, K2, V2> HashMap<K2, V2> castMap(Map<K1, V1> map, Function<K1, K2> keyCast, Function<V1, V2> valueCast, boolean keyNonnull)
	{
		return castMap(map, keyCast, valueCast, keyNonnull, false);
	}
			
	public static <K1, V1, K2, V2> HashMap<K2, V2> castMap(Map<K1, V1> map, Function<K1, K2> keyCast, Function<V1, V2> valueCast)
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

	/**
	 * Fill an array with elements from a collection. If the array capacity is lower, the rest elements will be ignored.
	 * If the array capacity is high, the rest will be filled with {@code null}.
	 * @return 1 if array is longer and some {@code null}s are added; 0 if the capacity is right equal; -1 if not all 
	 * elements are added.
	 */
	public static <T> int fillArray(T[] array, Collection<T> elemsFrom)
	{
		int i = 0;
		for (T elem: elemsFrom)
		{
			if (i >= array.length || i >= elemsFrom.size())
				break;
			array[i] = elem;
			++i;
		}
		if (array.length == elemsFrom.size())
			return 0;
		else if (array.length > elemsFrom.size())
		{
			for (; i < array.length; ++i)
			{
				array[i] = null;
			}
			return 1;
		}
		else return -1;
	}
	
	/**
	 * Generate a list of arithmetic sequence.
	 */
	public static ArrayList<Integer> intRangeList(int start, int endExcluded, int step)
	{
		int sizeAssumed = (endExcluded - start) / step + 10;
		ArrayList<Integer> out = new ArrayList<>(Math.min(10, sizeAssumed));
		int i = 0;
		int j = start;
		while (j < endExcluded)
		{
			out.set(i, j);
			i++;
			j += step;
		}
		return out;
	}
	
	/**
	 * Generate a list of arithmetic sequence.
	 */
	public static ArrayList<Double> doubleRangeList(double start, double endExcluded, int step)
	{
		int sizeAssumed = Mth.floor((endExcluded - start) / step) + 10;
		ArrayList<Double> out = new ArrayList<>(Math.min(10, sizeAssumed));
		int i = 0;
		double j = start;
		while (j < endExcluded)
		{
			out.set(i, j);
			i++;
			j += step;
		}
		return out;
	}

	/**
	 * Generate a raw array of arithmetic sequence.
	 */
	public static int[] intRangeArray(int start, int endExcluded, int step)
	{
		int size = Mth.floor((endExcluded - start) / step) + 1;
		int[] out = new int[size];
		int j = start;
		for (int i = 0; i < size; ++i)
		{
			out[i] = j;
			j += step;
		}
		return out;
	}
	
	/**
	 * Generate a raw array of arithmetic sequence.
	 */
	public static double[] doubleRangeArray(double start, double endExcluded, double step)
	{
		int size = Mth.floor((endExcluded - start) / step) + 1;
		double[] out = new double[size];
		double j = start;
		for (int i = 0; i < size; ++i)
		{
			out[i] = j;
			j += step;
		}
		return out;
	}
	
	/** Convert a generic list (modifiable or not) to an ArrayList. */
	public static <T> ArrayList<T> toArrayList(List<T> list)
	{
		ArrayList<T> arrayList = new ArrayList<T>();
		for (int i = 0; i < list.size(); ++i)
		{
			arrayList.add(list.get(i));
		}
		return arrayList;
			
	}

	/**
	 * Get a collection element that satisfies the condition with an input object.
	 * @return The satisfying element. If it contains multiple, randomly return one. If there's none, return null.
	 */
	@Nullable
	public static <T, U> U getSatisfies(T in, Collection<U> col, BiPredicate<T, U> condition)
	{
		for (U u: col)
		{
			if (condition.test(in, u))
				return u;
		}
		return null;
	}
	
	/**
	 * Add elements of a collection into a Hash set. It will only collect unique items. Equality is checked with custom predicate,
	 * not necessarily {@code Object#equals}. (To use {@code equals} you can use {@code Set#addAll.) 
	 * defined by the predicate within the same input set, it will randomly collect one.
	 * <p>Note: if two items are different with the predicate but {@code equals} returns true, it will still be consider 
	 * as duplication.
	 * <p>Note: complexity of this method is O(n^2), meaning it could be costly if called multiple times or in a loop.
	 */
	public static <T> void addAll(HashSet<T> set, Collection<T> col, BiPredicate<T, T> equalsPredicate)
	{
		for (T t: col)
		{
			if (getSatisfies(t, set, equalsPredicate) == null)
				set.add(t);
		}
	}

	/**
	 * Collect unique elements of a collection into a Hash set. Equality is checked with custom predicate,
	 * not necessarily {@code Object#equals}. (To use {@code equals} you can use {@code Set#addAll.) 
	 * defined by the predicate within the same input set, it will randomly collect one.
	 * <p>Note: if two items are different with the predicate but {@code equals} returns true, it will still be consider 
	 * as duplication.
	 * <p>Note: complexity of this method is O(n^2), meaning it could be costly if called multiple times or in a loop.
	 */
	public static <T> HashSet<T> collectUnique(Collection<T> col, BiPredicate<T, T> equalsPredicate) 
	{
		HashSet<T> res = new HashSet<>();
		addAll(res, col, equalsPredicate);
		return res;
	}
	
	/**
	 * Cast elements of a collection into another type, and collect unique items into a set. 
	 * Equality is checked with custom predicate, not necessarily {@code Object#equals}. (To use {@code equals},
	 * use {@code castSet} that's much faster.)
	 * <p>Note: if two items are different with the predicate but {@code equals} returns true, it will still be consider 
	 * as duplication.
	 * <p>Note: complexity of this method is O(n^2), meaning it could be costly if called multiple times or in a loop.
	 */
	public static <T1, T2> HashSet<T2> castUniqueToSet(Collection<T1> col, Function<T1, T2> cast, BiPredicate<T2, T2> equalsPredicate)
	{
		HashSet<T2> raw = new HashSet<>();
		for (T1 t1: col)
		{
			raw.add(cast.apply(t1));
		}
		return collectUnique(raw, equalsPredicate);
	}
	
	/**
	 * Cast elements of a collection into another type, and collect unique items into a set. 
	 * It uses {@code Object#equals} to check equality. 
	 * <p>Note: the result's size is not necessarily equal to the input, as duplicated cast result will be excluded.
	 */
	public static <T1, T2> HashSet<T2> castSet(Collection<T1> col, Function<T1, T2> cast)
	{
		HashSet<T2> res = new HashSet<>();
		for (T1 t1: col)
			res.add(cast.apply(t1));
		return res;
	}
	
	public static <T1, T2> ArrayList<T2> castList(List<T1> list, Function<T1, T2> cast)
	{
		ArrayList<T2> res = new ArrayList<>();
		for (T1 elem: list)
		{
			res.add(cast.apply(elem));
		}
		return res;
	}
	
	public static <T1, T2> void castListAndFill(List<T1> list, Function<T1, T2> cast, List<T2> fillInto)
	{
		fillInto.clear();
		for (T1 elem: list)
		{
			fillInto.add(cast.apply(elem));
		}
	}
	
	public static <T> HashSet<T> getRandomSubset(Set<T> parent, int subsetSize)
	{
		if (subsetSize > parent.size())
			throw new IllegalArgumentException("subsetSize is larger than parent size.");
		ArrayList<T> copy = iterableToList(parent);
		HashSet<T> res = new HashSet<>();
		for (int i = 0; i < subsetSize; ++i)
		{
			int pos = rnd.nextInt(copy.size());
			res.add(copy.get(pos));
			copy.remove(pos);
		}
		return res;
	}

}