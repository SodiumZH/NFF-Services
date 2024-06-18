package net.sodiumstudio.nautils.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.world.item.DyeColor;

/**
 * A collection for objects that have variants of the 16 vanilla dye colors, e.g. dyes, wools, terracottas, etc.
 */
public class WithDyeColors<T>
{
	private final Random rnd = new Random();
	
	private final HashMap<DyeColor, T> map = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public WithDyeColors(Object... keysAndValues)
	{
		DyeColor active = null;
		for (Object obj: keysAndValues)
		{
			if (active == null)
			{
				if (obj instanceof DyeColor color)
					active = color;
				else if (obj instanceof String str)
				{
					active = DyeColor.byName(str, null);
					if (active ==  null)
						throw new IllegalArgumentException("Illegal Color \"" + str + "\".");
				}
				else if (obj instanceof Integer i)
				{
					active = DyeColor.byId(i);
				}
				else throw new IllegalArgumentException("Illegal format. Should be: key, value, key, value, ...");
			}
			else
			{
				try {
					map.put(active, (T) obj);
				}
				catch(ClassCastException e) {
					throw new IllegalArgumentException("Illegal format. Should be: key, value, key, value, ...");
				}
			}
		}
	}
	
	/**
	 * Get the corresponding object of the given color.
	 */
	public T ofColor(DyeColor color)
	{
		return map.get(color);
	}
	
	/**
	 * Get the color of the object.
	 * @throws IllegalArgumentException if the object isn't in the table.
	 */
	public DyeColor getColor(T object)
	{
		for (DyeColor clr: map.keySet())
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
		return map.get(DyeColor.byId(rnd.nextInt(16)));
	}
	
	/**
	 * Get the object of the same color in another {@code WithStandardColors} of given item in this collection.
	 */
	public <U> U ofSameColor(T object, WithDyeColors<U> otherCollection)
	{
		return otherCollection.ofColor(this.getColor(object));
	}

	/**
	 * Get an array of all objects.
	 */
	@SuppressWarnings("unchecked")
	public T[] objectArray()
	{
		Object[] res = new Object[map.entrySet().size()];
		int i = 0;
		for (var entry: map.entrySet())
		{
			res[i] = entry.getValue();
			i++;
		}
		return (T[]) res;
	}
}
