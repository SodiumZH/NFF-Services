package net.sodiumstudio.nautils.math;

import java.util.ArrayList;
import java.util.Random;

/**
 * A {@code RandomSelection} is a series of objects that will be randomly selected.
 */
public class RandomSelection<T>
{

	protected Random rnd = new Random();
	protected ArrayList<Double> probSequence = new ArrayList<Double>();
	protected ArrayList<T> valSequence = new ArrayList<T>();
	protected T defaultVal = null;

	protected RandomSelection(T defaultValue)
	{
		defaultVal = defaultValue;
	}

	/**
	 * Create a {@code RandomSelection} with given default value.
	 * @param defaultValue Value if none of the added values are chosen.
	 */
	public static <T> RandomSelection<T> create(T defaultValue) {
		return new RandomSelection<T>(defaultValue);
	}

	/**
	 * Create a {@code RandomSelection} of double.
	 * @param defaultValue Value if none of the added values are chosen.
	 */
	public static DoubleRandomSelection createDouble(double defaultValue)
	{
		return new DoubleRandomSelection(defaultValue);
	}
	
	public RandomSelection<T> add(T value, double probability) {
		double last = this.probSequence.size() > 0 ? this.probSequence.get(probSequence.size() - 1) : 0d;
		probSequence.add(last + probability);
		valSequence.add(value);
		return this;
	}
	
	public RandomSelection<T> defaultValue(T value) {
		defaultVal = value;
		return this;
	}

	public T getValue() {
		double rnd = this.rnd.nextDouble();
		for (int i = 0; i < probSequence.size(); ++i)
		{
			if (rnd < probSequence.get(i))
				return valSequence.get(i);
		}
		return defaultVal;
	}
}
