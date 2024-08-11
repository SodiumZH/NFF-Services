package net.sodiumstudio.nautils.containers;

import net.minecraft.util.Tuple;

/**
 * Just a wrapper of {@link Tuple} that can display content in {@code toString}.
 */
public class Tuple2<A, B> extends Tuple<A, B>
{

	public Tuple2(A pA, B pB)
	{
		super(pA, pB);
	}

	@Override
	public String toString()
	{
		return String.format("Tuple2{A=%s, B=%s}", this.getA().toString(), this.getB().toString());
	}
	
}
