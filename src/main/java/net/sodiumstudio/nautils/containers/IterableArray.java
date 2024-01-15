package net.sodiumstudio.nautils.containers;

import java.util.Iterator;

/**
 * A wrapper of array for iteration.
 */
public class IterableArray<T> implements Iterable<T>
{

	private T[] array;
	
	public IterableArray(T[] array)
	{
		this.array = array;
	}
	
	@SafeVarargs
	public static <S> IterableArray<S> of(S... elems)
	{
		return new IterableArray<>(elems);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new IteratorImpl<>(array);
	}

	public T[] getArray()
	{
		return array;
	}
	
	private static class IteratorImpl<U> implements Iterator<U>
	{

		private final U[] array;
		private int currentIndex = 0;
		
		public IteratorImpl(U[] array)
		{
			this.array = array;
		}
		
		@Override
		public boolean hasNext() {
			return currentIndex < array.length;
		}

		@Override
		public U next() {
			currentIndex++;
			return array[currentIndex - 1];
		}
		
	}
	
}
