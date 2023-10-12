package net.sodiumstudio.nautils.function;

import java.util.HashMap;
import java.util.function.Predicate;

/**
 * A {@code MutablePredicate} is a series of {@link Predicate}s for a single object.
 * <p> It's composed with two part, the required and the optional lists. The object needs to satisfy all conditions in required and at least one condition in optional.
 */
public class MutablePredicate<T> implements Predicate<T>
{
	protected final HashMap<String, Predicate<T>> optionalPredicates = new HashMap<>();
	protected final HashMap<String, Predicate<T>> requiredPredicates = new HashMap<>();
	
	public MutablePredicate()
	{
		// Add this default check to prevent from being empty
		// This check will not do anything in fact
		requiredPredicates.put("default", t -> true);
	}
	
	/**
	 * Test with the whole predicate.
	 * <p> The object needs to satisfy all of predicates in required predicates (accessed with {@code getRequired()}) 
	 * and at least one predicate in optional predicates (accessed with {@code getOptional()}) to return {@code true}.
	 * <p> If optional or required predicate is empty, it will be ignored.
	 * <p> @throws UnsupportedOperationException If both optional and required predicates are empty.
	 */
	@Override
	public boolean test(T t) {
		
		boolean noOptional = optionalPredicates.isEmpty();
		boolean noRequired = requiredPredicates.isEmpty();
		
		if (noOptional && noRequired)
			throw new UnsupportedOperationException("Cannot invoke MutablePredicate#test because there's no valid predicate.");
		else if (noOptional && !noRequired)
		{
			for (Predicate<T> p: requiredPredicates.values())
			{
				if (!p.test(t))
					return false;
			}
			return true;
		}
		else if (!noOptional && noRequired)
		{
			for (Predicate<T> p: optionalPredicates.values())
			{
				if (p.test(t))
					return true;
			}
			return false;
		}
		else
		{	
			for (Predicate<T> p: requiredPredicates.values())
			{
				if (p != null && !p.test(t))
					return false;
			}
			for (Predicate<T> p: optionalPredicates.values())
			{
				if (p != null && p.test(t))
					return true;
			}
			return false;
		}
	}

	/**
	 * @return List of optional conditions. The test object needs to satisfy at least one of these predicates to satisfy the whole condition.
	 * <p> The key string is simply for labeling each predicate.
	 */
	public HashMap<String, Predicate<T>> getOptional()
	{
		return optionalPredicates;
	}
	
	/**
	 * @return List of required conditions. The test object needs to satisfy all of these predicates to satisfy the whole condition.
	 * <p> The key string is simply for labeling each predicate.
	 */
	public HashMap<String, Predicate<T>> getRequired()
	{
		return requiredPredicates;
	}
	
	/**
	 * Put a new predicate to optional conditions.
	 */
	public MutablePredicate<T> putOptional(String key, Predicate<T> predicate)
	{
		this.optionalPredicates.put(key, predicate);
		return this;
	}
	
	/**
	 * Put a new predicate to required conditions.
	 */
	public MutablePredicate<T> putRequired(String key, Predicate<T> predicate)
	{
		this.requiredPredicates.put(key, predicate);
		return this;
	}
	
}
