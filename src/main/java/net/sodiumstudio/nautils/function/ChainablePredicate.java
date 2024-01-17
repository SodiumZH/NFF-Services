package net.sodiumstudio.nautils.function;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * A sequence of predicates that can be appended with AND operation (but not OR). Each predicate will be stored separately to prevent endless recursion.
 */
public class ChainablePredicate<T>
{
	protected ArrayList<Predicate<T>> predicates = new ArrayList<>();
	
	public ChainablePredicate(Predicate<T> predicate)
	{
		predicates.add(predicate);
	}
	
	public ChainablePredicate()
	{
	}
	
	public ChainablePredicate<T> and(Predicate<T> other)
	{
		predicates.add(other);
		return this;
	}
	
	public boolean test(T obj)
	{
		for (int i = 0; i < predicates.size(); ++i)
		{
			if (!predicates.get(i).test(obj))
				return false;
		}
		return true;
	}
	
}
