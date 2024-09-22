package net.sodiumzh.nautils.math;

import java.util.ArrayList;

import net.minecraft.util.RandomSource;

/**
 * A {@code RandomSelection} is a series of objects that will be randomly selected.
 * <p>Usage example:
 * <p>{@code new RandomSelection<Integer>(0).add(1, 0.1d).add(2, 0.3d).add(3, 0.5d)}
 * <p>Then the probabilities are: 1:10%; 2:30%; 3:50%; 0:10% (1-10%-30%-50%).
 */
public class RandomSelection<T>
{

	protected static final RandomSource RND = RandomSource.create();
	protected ArrayList<Double> probSequence = new ArrayList<Double>();
	protected ArrayList<T> valSequence = new ArrayList<T>();
	protected T defaultVal = null;

	/**
	 * Create with given fallback value.
	 */
	public RandomSelection(T fallback)
	{
		defaultVal = fallback;
	}

	/**
	 * @deprecated Use the constructor instead.
	 */
	@Deprecated
	public static <T> RandomSelection<T> create(T fallback) {
		return new RandomSelection<T>(fallback);
	}

	@Deprecated
	public static DoubleRandomSelection createDouble(double fallback)
	{
		return new DoubleRandomSelection(fallback);
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
	
	/**
	 * Set the default value i.e. the value if none of the added values are selected.
	 */
	public RandomSelection<T> defaultValue(T value) {
		defaultVal = value;
		return this;
	}

	/**
	 * @deprecated use {@code select} instead.
	 */
	@Deprecated
	public T getValue(RandomSource rnd)
	{
		return this.select(rnd);
	}
	
	/**
	 * @deprecated use {@code select} instead.
	 */
	@Deprecated
	public T getValue()
	{
		return this.select();
	}
	
	/**
	 * Random pick an element with given random source.
	 */
	public T select(RandomSource rnd) {
		double rnddouble = rnd.nextDouble();
		for (int i = 0; i < probSequence.size(); ++i)
		{
			if (rnddouble < probSequence.get(i))
				return valSequence.get(i);
		}
		return defaultVal;
	}
	
	/**
	 * Random pick an element with internal random source.
	 */
	public T select() {
		return this.select(RND);
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
