package net.sodiumstudio.nautils.math;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

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
	
	/**
	 * Get BlockPos from double xyz.
	 * This method is in place of BlockPos#new(double, double, double) because 1.20 removed it.
	 */
	public static BlockPos getBlockPos(double x, double y, double z)
	{
		return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}
	
	/**
	 * Get BlockPos from Vec3.
	 * This method is in place of BlockPos#new(Vec3) because 1.20 removed it.
	 */
	public static BlockPos getBlockPos(Vec3 v)
	{
		return getBlockPos(v.x, v.y, v.z);
	}
	
}
