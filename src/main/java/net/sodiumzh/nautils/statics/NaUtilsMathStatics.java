package net.sodiumzh.nautils.statics;

import java.util.Random;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NaUtilsMathStatics
{
	private static final Random RND = new Random();
	
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

	/**
	 * Get the squared closest distance of the surfaces of two boxes.
	 */
	public static double getBoxSurfaceDistSqr(AABB box1, AABB box2)
	{
		double deltaX = 0;
		double deltaY = 0;
		double deltaZ = 0;
		if (box1.maxX < box2.minX)
			deltaX = box2.minX - box1.maxX;
		else if (box1.minX > box2.maxX)
			deltaX = box1.minX - box2.maxX;
		if (box1.maxY < box2.minY)
			deltaY = box2.minY - box1.maxY;
		else if (box1.minY > box2.maxY)
			deltaY = box1.minY - box2.maxY;
		if (box1.maxZ < box2.minZ)
			deltaZ = box2.minZ - box1.maxZ;
		else if (box1.minZ > box2.maxZ)
			deltaZ = box1.minZ - box2.maxZ;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}
	
	/**
	 * Get the squared closest distance of the surfaces of a box and an irregular shape. The latter is represented with a combination of several boxes.
	 */
	public static double getBoxSurfaceDistSqrToIrregular(AABB box, AABB[] irregularShape)
	{
		if (irregularShape.length == 0)
			throw new IllegalArgumentException("Empty shape.");
		double dist = getBoxSurfaceDistSqr(box, irregularShape[0]);
		for (int i = 1; i < irregularShape.length; ++i)
		{
			dist = Math.min(dist, getBoxSurfaceDistSqr(box, irregularShape[i]));
		}
		return dist;
	}
	
	/**
	 * Get the angle (theta) from vector xy, in radians. Range: (-pi, pi]
	 */
	public static double getAngleRadian(double vec2X, double vec2Y)
	{
		Vec3 v = new Vec3(vec2X, vec2Y, 0).normalize();
		if (vec2X * vec2X + vec2Y * vec2Y < 1e-12d)
		{
			LogUtils.getLogger().warn("NaUtils: NaUtilsMathStatics#getAngleRadian: input vector is too short. Result may be inaccurate.");
		}
		// Handle zero cases
		if (Math.abs(v.x) == 0d && Math.abs(v.y) == 0d)
			return 0;
		else if (Math.abs(v.x) == 0d)
			return (v.y > 0 ? Math.PI / 2 : -Math.PI / 2);
		else if (Math.abs(v.y) == 0d)
			return (v.x > 0 ? 0 : Math.PI);
		// 1st & 2nd quadrant
		if (v.y > 0)
			return Math.acos(v.x);
		// 4th quadrant
		else if (v.x > 0)
			return Math.asin(v.y);
		// 3rd quadrant
		else return Math.acos(-v.x) - Math.PI;
	}
	
	
	/**
	 * Rotate a Vec3 around Y axis with given angle in degrees. Positive angle represents anticlockwise rotation looked from upside. 
	 */
	public static Vec3 rotateVectorY(Vec3 v, double angleDegrees)
	{
		double xzScale = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
		if (xzScale < 1e-12d)
		{
			LogUtils.getLogger().warn("NaUtils: NaUtilsMathStatics#rotateY: input vector is too short. Result may be inaccurate.");
			return v;
		}
		Vec3 xzNorm = new Vec3(v.x, 0d, v.z).normalize();
		double angleRadians = angleDegrees * Math.PI / 180d;
		double initAngleRadian = getAngleRadian(xzNorm.x, xzNorm.z);
		return new Vec3(Math.cos(initAngleRadian + angleRadians), 0d, Math.sin(initAngleRadian + angleRadians))
				.scale(xzScale).add(0, v.y, 0);
	}
	
	/**
	 * Referred to UE4
	 */
	public static Vec3 rotateVector(Vec3 v, Vec3 axis, double angleDegrees)
	{
		if (axis.length() < 1e-12d)
			LogUtils.getLogger().warn("NaUtils: NaUtilsMathStatics#rotate: input vector is too short. Result may be inaccurate.");
			
		Vec3 axisNorm = axis.normalize();
		double radian = angleDegrees * Math.PI / 180d;
		double s = Math.sin(radian);
		double c = Math.cos(radian);

		double xx = axisNorm.x * axisNorm.x;
		double yy = axisNorm.y * axisNorm.y;
		double zz = axisNorm.z * axisNorm.z;

		double xy = axisNorm.x * axisNorm.y;
		double yz = axisNorm.y * axisNorm.z;
		double zx = axisNorm.z * axisNorm.x;

		double xs = axisNorm.x * s;
		double ys = axisNorm.y * s;
		double zs = axisNorm.z * s;

		double omc = 1.d - c;

		return new Vec3(
			(omc * xx + c) * v.x + (omc * xy - zs) * v.y + (omc * zx + ys) * v.z,
			(omc * xy + zs) * v.x + (omc * yy + c) * v.y + (omc * yz - xs) * v.z,
			(omc * zx - ys) * v.x + (omc * yz + xs) * v.y + (omc * zz + c) * v.z
			);
	}
	
	
	/**
	 * Get a random unit vector with uniform-distribution on sphere surface area.
	 */
	public static Vec3 randomUnitVector()
	{
		while (true)
		{
			Vec3 v = new Vec3(RND.nextDouble() * 2d - 1d, RND.nextDouble() * 2d - 1d, RND.nextDouble() * 2d - 1d);
			if (v.lengthSqr() <= 1d)
				return v.normalize();
		}
	}
	
	/** Get a random vector of given length, uniform in orientation (probability in direct proportion of sphere surface area) */
	public static Vec3 randomVector(double scale)
	{
		return randomUnitVector().scale(scale);
	}
	
	/** Random vector pointing to a oval surface */
	public static Vec3 randomOvalVector(Vec3 scale)
	{
		Vec3 v = randomUnitVector();
		return new Vec3(v.x * scale.x, v.y * scale.y, v.z * scale.z);
	}
	
	/** Random vector pointing to a oval surface */
	public static Vec3 randomOvalVector(double xScale, double yScale, double zScale)
	{
		return randomOvalVector(new Vec3(xScale, yScale, zScale));
	}

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
