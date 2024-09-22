package net.sodiumzh.nautils.function;

import java.util.ArrayList;
import java.util.function.BiPredicate;

/**
 * A sequence of bi-predicates that can be appended with AND operation (but not OR). Each bi-predicate will be stored separately 
 * to prevent endless recursion. (Like: {@code condition = i -> condition.test(i) && other.test(i)})
 */
public class ChainableBiPredicate<T1, T2>
{
	protected ArrayList<BiPredicate<T1, T2>> predicates = new ArrayList<>();
	
	public ChainableBiPredicate(BiPredicate<T1, T2> predicate)
	{
		predicates.add(predicate);
	}
	
	public ChainableBiPredicate()
	{
	}
	
	public ChainableBiPredicate<T1, T2> and(BiPredicate<T1, T2> other)
	{
		predicates.add(other);
		return this;
	}
	
	public boolean test(T1 t1, T2 t2)
	{
		for (int i = 0; i < predicates.size(); ++i)
		{
			if (!predicates.get(i).test(t1, t2))
				return false;
		}
		return true;
	}
}
