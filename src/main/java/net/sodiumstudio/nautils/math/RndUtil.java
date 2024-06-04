package net.sodiumstudio.nautils.math;

import java.util.Collection;
import java.util.Random;

import net.minecraft.util.RandomSource;

public class RndUtil {
	
	protected static final RandomSource RND = RandomSource.create();
	
	public static double rndRangedDouble(double min, double max)
	{
		return RND.nextDouble() * (max - min) + min ;
	}

	public static float rndRangedFloat(float min, float max)
	{
		return RND.nextFloat() * (max - min) + min ;
	}
	
	public static <T> T randomPick(T[] array)
	{
		return array[RND.nextInt(array.length)];
	}

	public static int rndPoisson(int lambda, float k)
	{
		int res = 0;
		for (int i = 0; i < lambda; ++i)
		{
			if (RND.nextFloat() < k)
			{
				res++;
			}
		}
		return res;
	}
	
	
	
}
