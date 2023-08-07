package net.sodiumstudio.nautils.math;

public class MathUtil
{
	public static double max(double... vals)
	{
		if (vals.length == 0)
			throw new IllegalArgumentException("Missing params");
		double res = vals[0];
		for (int i = 1; i < vals.length; ++i)
		{
			res = Math.max(res, vals[i]);
		}
		return res;
	}
	
	public static int max(int... vals)
	{
		if (vals.length == 0)
			throw new IllegalArgumentException("Missing params");
		int res = vals[0];
		for (int i = 1; i < vals.length; ++i)
		{
			res = Math.max(res, vals[i]);
		}
		return res;
	}
	
	public static double min(double... vals)
	{
		if (vals.length == 0)
			throw new IllegalArgumentException("Missing params");
		double res = vals[0];
		for (int i = 1; i < vals.length; ++i)
		{
			res = Math.min(res, vals[i]);
		}
		return res;
	}
	
	public static int min(int... vals)
	{
		if (vals.length == 0)
			throw new IllegalArgumentException("Missing params");
		int res = vals[0];
		for (int i = 1; i < vals.length; ++i)
		{
			res = Math.min(res, vals[i]);
		}
		return res;
	}
	
	public static int sum(int... vals)
	{
		int res = 0;
		for (int i: vals)
			res += i;
		return res;
	}
	
	public static double sum(double... vals)
	{
		double res = 0;
		for (double i: vals)
			res += i;
		return res;
	}
	
	public static double avr(double... vals)
	{
		double res = 0;
		for (double i : vals)
		{
			res += (i / vals.length);
		}
		return res;
	}
}
