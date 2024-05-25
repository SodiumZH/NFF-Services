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
	
	/**
	 * Add a result and its probability.
	 * <p>Note: if the accumulated probability is over 1, the entries added later may change or be omitted.
	 * For example, if there's a sequence: (1, 10%; 2, 25%; 3, 40%; 4, 60%; 5, 85%), then the actual
	 * probability is: (1, 10%; 2, 25%; 3, 40%; 4, 25%).
	 * <p>You can use {@code scaleTo} to scale the overall probability to a given value, or {@code normalize} to scale to 1. 
	 */
	public RandomSelection<T> add(T value, double probability) {
		if (probability < 0)
			throw new IllegalArgumentException();
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
	
	/**
	 * Check if this RandomSelection contains any valid entry. If this
	 * is false, the value will be always the default value.
	 */
	public boolean containsEntry()
	{
		return probSequence.size() > 0;
	}
	
	/**
	 * Get the probability not to pick the default value.
	 * <p>It could be above 1. In this case some probabilities will be wrong. See {@link RandomSelection#add}.
	 */
	public double getNonDefaultProbability()
	{
		if (!containsEntry())
			return 0d;
		return probSequence.get(probSequence.size() - 1);
	}
	
	/**
	 * Scale all probabilities in proportion to make the overall non-default probability become the input value.
	 */
	public RandomSelection<T> scaleTo(double overallProbability)
	{
		if (overallProbability < 0)
			throw new IllegalArgumentException();
		if (!containsEntry())
			return this;
		double factor = overallProbability / getNonDefaultProbability();
		for (int i = 0; i < probSequence.size(); ++i) 
		{
			probSequence.set(i, probSequence.get(i) * factor);
		}
		return this;
	}
	
	/**
	 * Scale all probabilities in proportion to make the overall non-default probability become 1.
	 */
	public RandomSelection<T> normalize()
	{
		return scaleTo(1d);
	}
	
}
