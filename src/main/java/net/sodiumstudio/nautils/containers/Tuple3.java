package net.sodiumstudio.nautils.containers;

/**
 * A container with 3 elements of any types.
 */
public class Tuple3<A, B, C>
{
	public A a;
	public B b;
	public C c;
	
	public Tuple3(A a, B b, C c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
