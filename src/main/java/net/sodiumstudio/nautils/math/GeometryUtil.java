package net.sodiumstudio.nautils.math;

import java.util.Random;

import com.mojang.logging.LogUtils;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Mathematical utilities related to 3D geometry e.g. Vec3, AABB, etc.
 */
public class GeometryUtil
{

	public static Random rnd = new Random();
	
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
			LogUtils.getLogger().warn("NaUtils: GeometryUtils#getAngleRadian: input vector is too short. Result may be inaccurate.");
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
	public static Vec3 rotateY(Vec3 v, double angleDegrees)
	{
		double xzScale = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
		if (xzScale < 1e-12d)
		{
			LogUtils.getLogger().warn("NaUtils: GeometryUtils#rotateY: input vector is too short. Result may be inaccurate.");
			return v;
		}
		Vec3 xzNorm = new Vec3(v.x, 0d, v.z).normalize();
		double angleRadians = angleDegrees * Math.PI / 180d;
		double initAngleRadian = getAngleRadian(xzNorm.x, xzNorm.z);
		return new Vec3(Math.cos(initAngleRadian + angleRadians), 0d, Math.sin(initAngleRadian + angleRadians))
				.scale(xzScale).add(0, v.y, 0);
	}
	
	/**
	 * Get a random unit vector with uniform-distribution on sphere surface area.
	 */
	public static Vec3 randomVector()
	{
		while (true)
		{
			Vec3 v = new Vec3(rnd.nextDouble() * 2d - 1d, rnd.nextDouble() * 2d - 1d, rnd.nextDouble() * 2d - 1d);
			if (v.x * v.x + v.y * v.y + v.z * v.z <= 1d)
				return v.normalize();
		}
	}
}
