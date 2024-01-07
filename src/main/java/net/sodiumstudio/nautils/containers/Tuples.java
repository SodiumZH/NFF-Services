package net.sodiumstudio.nautils.containers;

public class Tuples
{
	public static <A> A getA(Tuple3<A, ?, ?> tuple) {return tuple.a;}
	public static <B> B getB(Tuple3<?, B, ?> tuple) {return tuple.b;}
	public static <C> C getC(Tuple3<?, ?, C> tuple) {return tuple.c;}
	public static <D> D getD(Tuple4<?, ?, ?, D> tuple) {return tuple.d;}
}
