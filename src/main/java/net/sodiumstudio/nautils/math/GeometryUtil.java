package net.sodiumstudio.nautils.math;

import net.minecraft.world.phys.AABB;

/**
 * Mathematical utilities related to 3D geometry e.g. Vec3, AABB, etc.
 */
public class GeometryUtil
{

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
}
