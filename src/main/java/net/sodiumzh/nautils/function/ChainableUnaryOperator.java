package net.sodiumzh.nautils.function;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

/**
 * A sequence of unary operators that can be appended to be executed in sequence in the order they're added.
 * Each operator will be stored separately to prevent endless recursion.
 */
public class ChainableUnaryOperator<T>
{
	protected ArrayList<UnaryOperator<T>> operators = new ArrayList<>();
	
	public ChainableUnaryOperator() {}
	
	public ChainableUnaryOperator(UnaryOperator<T> operator)
	{
		operators.add(operator);
	}
	
	/**
	 * Add next operation.
	 */
	public ChainableUnaryOperator<T> then(UnaryOperator<T> nextOperator)
	{
		operators.add(nextOperator);
		return this;
	}
	
	/**
	 * Do the operations to an input object.
	 */
	public T apply(T in)
	{
		if (operators.size() == 0)
			return in;
		T out = in;
		for (int i = 0; i < operators.size(); ++i)
		{
			out = operators.get(i).apply(out);
		}
		return out;
	}
	
}
