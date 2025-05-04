package net.sodiumzh.nfu.container;

import java.util.Objects;

/**
 * A container with 4 elements of any types.
 */
public class Tuple4<A, B, C, D>
{
	public A a;
	public B b;
	public C c;
	public D d;
	
	public Tuple4(A a, B b, C c, D d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	@Override
	public String toString()
	{
		return "Tuple4{a=" + this.a.toString() + ", b=" + this.b.toString() + ", c=" + this.c.toString() + ", d=" + this.d.toString() + "}";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Tuple4<?, ?, ?, ?> otherTuple
				&& Objects.equals(this.a, otherTuple.a)
				&& Objects.equals(this.b, otherTuple.b)
				&& Objects.equals(this.c, otherTuple.c)
				&& Objects.equals(this.d, otherTuple.d);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c, d);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {

		return new Tuple4<>(a, b, c, d);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(Tuple3<A, B, C> abc, D d) {

		return new Tuple4<>(abc.a, abc.b, abc.c, d);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, Tuple3<B, C, D> bcd) {

		return new Tuple4<>(a, bcd.a, bcd.b, bcd.c);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(Tuple2<A, B> ab, C c, D d) {

		return new Tuple4<>(ab.getA(), ab.getB(), c, d);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, Tuple2<B, C> bc, D d) {

		return new Tuple4<>(a, bc.getA(), bc.getB(), d);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, Tuple2<C, D> cd) {

		return new Tuple4<>(a, b, cd.getA(), cd.getB());
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(Tuple2<A, B> ab, Tuple2<C, D> cd) {

		return new Tuple4<>(ab.getA(), ab.getB(), cd.getA(), cd.getB());
	}
}
