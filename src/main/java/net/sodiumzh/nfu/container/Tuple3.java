package net.sodiumzh.nfu.container;

import java.util.Objects;

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
	
	@Override
	public String toString()
	{
		return "Tuple3{a=" + this.a.toString() + ", b=" + this.b.toString() + ", c=" + this.c.toString() + "}";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Tuple3<?, ?, ?> otherTuple
				&& Objects.equals(this.a, otherTuple.a)
				&& Objects.equals(this.b, otherTuple.b)
				&& Objects.equals(this.c, otherTuple.c);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
		return new Tuple3<>(a, b, c);
	}

	public static <A, B, C> Tuple3<A, B, C> of(Tuple2<A, B> ab, C c) {
		return new Tuple3<>(ab.getA(), ab.getB(), c);
	}

	public static <A, B, C> Tuple3<A, B, C> of(A a, Tuple2<B, C> bc) {
		return new Tuple3<>(a, bc.getA(), bc.getB());
	}


}
