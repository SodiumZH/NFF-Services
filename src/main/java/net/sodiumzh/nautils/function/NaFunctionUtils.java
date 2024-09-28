package net.sodiumzh.nautils.function;

import java.util.function.Predicate;

public class NaFunctionUtils
{

	/**
	 * Check if all objects in the array satisfy the given predicate.
	 * <p> Note: throws if the array is empty.
	 */
	public static <T> boolean and(Predicate<T> predicate, T[] toTest)
	{
		if (toTest.length == 0)
			throw new IllegalArgumentException("Empty array to test.");
		for (int i = 0; i < toTest.length; ++i)
		{
			if (!predicate.test(toTest[i]))
				return false;
		}
		return true;
	}

	/**
	 * Check if at least one object in the array satisfies the given predicate.
	 * <p> Note: throws if the array is empty.
	 */
	public static <T> boolean or(Predicate<T> predicate, T[] toTest)
	{
		if (toTest.length == 0)
			throw new IllegalArgumentException("Empty array to test.");
		for (int i = 0; i < toTest.length; ++i)
		{
			if (predicate.test(toTest[i]))
				return true;
		}
		return false;
	}
	
}
