package net.sodiumstudio.nautils.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * A table for objects that have variants of the 16 vanilla standard colors, e.g. dyes, wools, terracottas, etc.
 */
public class WithStandardColors<T>
{
	private final Random rnd = new Random();
	
	private final HashMap<StandardColor, T> map;

	@SafeVarargs
	private WithStandardColors (T... objects) // Order: white, black, gray, lightGray, red, green, blue, yellow, lightBlue, magenta, cyan, orange, lime, purple, brown, pink
	{
		map = new HashMap<>();
		if (objects.length != 16)
			throw new IllegalArgumentException("wrong size.");
		for (int i = 0; i < 15; ++i)
		{
			map.put(StandardColor.ofId(i), objects[i]);
		}
		// Perform duplication check
		ArrayList<T> existingObjs = new ArrayList<>(16);
		for (T val: map.values())
		{
			if (existingObjs.contains(val))
				throw new IllegalArgumentException("duplicate entries.");
			else existingObjs.add(val);
		}
	}
	
	public static <U> WithStandardColors<U> of(U white, U black, U gray, U lightGray,
			U red, U green, U blue, U yellow, U lightBlue, U magenta, U cyan, U orange, U lime, U purple, U brown, U pink)
	{
		return new WithStandardColors<U>(white, black, gray, lightGray, 
				red, green, blue, yellow, lightBlue, magenta, cyan, orange, lime, purple, brown, pink);
	}
	
	/**
	 * Get the corresponding object of the given color.
	 */
	public T ofColor(StandardColor color)
	{
		return map.get(color);
	}
	
	/**
	 * Get the color of the object.
	 * @throws IllegalArgumentException if the object isn't in the table.
	 */
	public StandardColor getColor(T object)
	{
		for (StandardColor clr: map.keySet())
		{
			if (map.get(clr).equals(object))
				return clr;
		}
		throw new IllegalArgumentException("WithStandardColor#getColor: input object isn't an element of the colored object list.");
	}

	/**
	 * Check if the table contains the given object.
	 */
	public boolean contains(T object)
	{
		return map.values().contains(object);
	}
	
	/**
	 * Pick an object of random color.
	 */
	public T ofRandomColor()
	{
		return map.get(StandardColor.ofId(rnd.nextInt(16)));
	}
	
}
