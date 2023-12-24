package net.sodiumstudio.nautils.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
	
	/**
	 * Convert a {@link Function} to {@link Supplier} with given parameter.
	 */
	public static <T, U> Supplier<U> withParam(Function<T, U> function, T param)
	{
		return () -> function.apply(param);
	}
	
	/**
	 * Convert a {@link Function} to {@link Supplier} with given parameter supplier.
	 */
	public static <T, U> Supplier<U> withParam(Function<T, U> function, Supplier<T> paramSupplier)
	{
		return () -> function.apply(paramSupplier.get());
	}
	
	/**
	 * Convert a {@link Consumer} to {@link Runnable} with given parameter.
	 */	
	public static <T> Runnable withParam(Consumer<T> consumer, T param)
	{
		return () -> consumer.accept(param);
	}
	
	/**
	 * Convert a {@link Consumer} to {@link Runnable} with given parameter supplier.
	 */	
	public static <T> Runnable withParam(Consumer<T> consumer, Supplier<T> paramSupplier)
	{
		return () -> consumer.accept(paramSupplier.get());
	}
}
