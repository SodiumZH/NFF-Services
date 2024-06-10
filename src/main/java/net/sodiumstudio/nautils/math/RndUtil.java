package net.sodiumstudio.nautils.math;

import java.util.Random;

public class RndUtil {
	
	protected static final Random RND = new Random();
	
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
