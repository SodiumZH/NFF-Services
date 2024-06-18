package net.sodiumstudio.nautils.math;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.util.RandomSource;

/**
 * A {@code WeightedRandomSelector} contains a set of objects with probability weights. It will randomly pick one of the elements with
 * probabilities depending on the weights.
 * <p>The different between this and {@code RandomSelection} is that {@code WeightedRandomSelector} receives any double value as weights
 * and probabilities will be automatically calculated, while {@code RandomSelection} receives probabilities themselves and has a fallback
 * value if none of the input values are picked.
 * <p>Usage example:
 * <p>{@code new WeightedRandomSelector<Integer>().add(1, 1.0d).add(2, 3.0d).add(3, 4.0d)}
 * <p>Then the probabilities are: 1=12.5%, 2=37.5%, 3=50.0% (probabilites are proportional to the weight).
 */
public class WeightedRandomSelector<T>
{
	private static final RandomSource RND = RandomSource.create();
	private final Map<T, Double> objs = new HashMap<>();
	private final double nullWeight = 0d;
	
	public WeightedRandomSelector() {}
	
	public WeightedRandomSelector<T> add(@Nullable T value, double weight)
	{
		if (weight < 0)
			throw new IllegalArgumentException("WeightedRandomSelector: negative weight.");
		
		objs.put(value, weight);
		return this;
	}
	
	public boolean isNullable()
	{
		return nullWeight > 0;
	}
	
	public T select(RandomSource rnd)
	{
		double weightSum = 0d;
		for (var entry: objs.entrySet())
		{
			weightSum += entry.getValue();
		}
		weightSum += this.nullWeight;
		if (weightSum == 0)
			throw new IllegalStateException("WeightedRandomSelector#pick: No valid entries.");
		RandomSelection<T> s = RandomSelection.create(null);
		for (var entry: objs.entrySet())
		{
			s.add(entry.getKey(), entry.getValue() / weightSum);
		}
		s.add(null, nullWeight / weightSum);
		// For nullable, the small probability of taking the fallback (null) could be accounted as null and ignored
		if (isNullable())
			return s.select(rnd);
		// If not nullable, taking null fallback could cause unexpected error, so re-pick if it accidently picked the fallback null
		else {
			T res = null;
			for (int i = 0; i < 100; ++i)
			{
				res = s.select(rnd);
				if (res != null) return res;
			}
			throw new RuntimeException("WeightedRandomSelector#pick: failed to pick a valid value. This indicates an intrinsic bug in WeightedRandomSelector class itself.");
		}
	}
	
	public T select()
	{
		return this.select(RND);
	}
	
}
