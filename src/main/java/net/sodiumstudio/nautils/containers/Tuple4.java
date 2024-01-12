package net.sodiumstudio.nautils.containers;

/**
 * A container with 4 elements of any types.
 */
public class Tuple4<A, B, C, D> extends Tuple3<A, B, C>
{
	public D d;
	
	public Tuple4(A a, B b, C c, D d)
	{
		super(a, b, c);
		this.d = d;
	}
}
