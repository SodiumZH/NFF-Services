package net.sodiumzh.nfu.util;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.math.ThreadSafeRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class NFUMathStatics
{
	public static final RandomSource RND = new ThreadSafeRandomSource();

	@Deprecated
	public static double max(double... vals)
	{
		return DoubleStream.of(vals).max().orElseThrow();
	}

	@Deprecated
	public static int max(int... vals)
	{
		return IntStream.of(vals).max().orElseThrow();
	}

	@Deprecated
	public static double min(double... vals)
	{
		return DoubleStream.of(vals).min().orElseThrow();
	}

	@Deprecated
	public static int min(int... vals)
	{
		return IntStream.of(vals).min().orElseThrow();
	}

	@Deprecated
	public static int sum(int... vals)
	{
		return IntStream.of(vals).sum();
	}

	@Deprecated
	public static double sum(double... vals)
	{
		return DoubleStream.of(vals).sum();
	}

	@Deprecated
	public static double avr(double... vals)
	{
		return DoubleStream.of(vals).average().orElseThrow();
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

	// 3D Geometry //

	/**
	 * Get the squared closest distance of the surfaces of two boxes.
	 */
	public static double getBoxSurfaceDistSqr(AABB box1, AABB box2)
	{
		double dx = max(0, box1.minX - box2.maxX, box2.minX - box1.maxX);
		double dy = max(0, box1.minY - box2.maxY, box2.minY - box1.maxY);
		double dz = max(0, box1.minZ - box2.maxZ, box2.minZ - box1.maxZ);

		return dx * dx + dy * dy + dz * dz;
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
	public static double getAngleRadian(double x, double y)
	{
		if (x == 0.0 && y == 0.0) {
			return 0;
		}

        return Math.atan2(y, x);
	}
	
	
	/**
	 * Rotate a Vec3 around Y axis with given angle in degrees. Positive angle represents anticlockwise rotation looked from upside. 
	 */
	public static Vec3 rotateVectorY(Vec3 v, double angleDegrees)
	{
		if (v.x * v.x + v.z * v.z < 1e-12) return v;
		double angle = Math.toRadians(angleDegrees);
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);

		// Transform matrix:
		// [ cosθ  0 -sinθ] [x]
		// [   0   1    0 ] [y]
		// [sinθ   0  cosθ] [z]
		double newX = v.x * cos + -v.z * sin;
		double newZ = v.x * sin + v.z * cos;

		return new Vec3(newX, v.y, newZ);
	}

	/**
	 * Transform a position's internal coordination in a box (center=(0,0,0), corners=(1,1,1),(-1,-1,-1) etc.)
	 * to the absolute coordination.
	 */
	public static Vec3 relToAbs(Vec3 rel, AABB box) {
		return box.getCenter().add(box.getCenter().subtract(box.minX, box.minY, box.minZ).multiply(rel));
	}

	/**
	 * Transform a bounding box defined with internal coordination in another box (center=(0,0,0),
	 * corners=(1,1,1),(-1,-1,-1) etc.) to the absolute coordination.
	 */
	public static AABB relToAbs(AABB rel, AABB box) {
		return new AABB(relToAbs(new Vec3(rel.minX, rel.minY, rel.minZ), box),
			relToAbs(new Vec3(rel.maxX, rel.maxY, rel.maxZ), box));
	}

	/**
	 * Referred to UE4
	 */
	public static Vec3 rotateVector(Vec3 v, Vec3 axis, double angleDegrees)
	{
		if (axis.length() < 1e-12d)
			NFUDebugStatics.warnOnce(NFUMathStatics.class, "NaUtils: NaUtilsMathStatics#rotate: input vector is too short. Result may be inaccurate.");
			
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
	
	// Random //

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
	
	/** Random vector pointing to an oval surface */
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

	public static Vec3 rndPosition(AABB border) {
		return new Vec3(rndRangedDouble(border.minX, border.maxX), rndRangedDouble(border.minY, border.maxY),
			rndRangedDouble(border.minZ, border.maxZ));
	}

	/**
	 * Get a block position stream in an octahedral area of which the Manhattan distance to a given center pos is no further
	 * than the given distance.
	 */
	public static Stream<BlockPos> withinManhattanDistance(BlockPos center, int distance) {
		return BlockPos.betweenClosedStream(new BlockPos(center.getX() - distance, center.getY() - distance, center.getZ() - distance),
			new BlockPos(center.getX() + distance, center.getY() + distance, center.getZ() + distance))
			.filter(pos -> center.distManhattan(pos) <= distance).map(pos -> new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
	}

	/**
	 * Get a block position stream on an octahedral surface of which the Manhattan distance to a given center pos equals
	 * to the given distance.
	 */
	public static Stream<BlockPos> atManhattanDistance(BlockPos center, int distance) {
		return BlockPos.betweenClosedStream(new BlockPos(center.getX() - distance, center.getY() - distance, center.getZ() - distance),
				new BlockPos(center.getX() + distance, center.getY() + distance, center.getZ() + distance))
			.filter(pos -> center.distManhattan(pos) == distance).map(pos -> new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
	}


	private static final ArrayList<Integer> FIBONACCI_SEQUENCE = new ArrayList<>(60);

	static {
		FIBONACCI_SEQUENCE.add(0);
		FIBONACCI_SEQUENCE.add(1);
		for (int i = 2; true; ++i) {
			if (FIBONACCI_SEQUENCE.get(i - 1) > Integer.MAX_VALUE - FIBONACCI_SEQUENCE.get(i - 2)) break;
			FIBONACCI_SEQUENCE.add(FIBONACCI_SEQUENCE.get(i - 2) + FIBONACCI_SEQUENCE.get(i - 1));
		}
	}

	/**
	 * Get the Fibonacci number if it's within INT_MAX. Or empty if it's not.
	 */
	public static Optional<Integer> getFibonacci(int index) {
		return (index >= 0 && index < FIBONACCI_SEQUENCE.size()) ? Optional.of(FIBONACCI_SEQUENCE.get(index)) : Optional.empty();
	}
	/**
	 * Randomly pick a given amount of integers from the range [0, maxEx) successively and fill into a list.
	 * @param maxEx Upper bound of the integers (excluding, i.e. range = [0, maxEx))
	 * @param amount Amount of picked integers.
	 * @param unique If true, each element of the output sequence will be unique.
	 * @param rnd Random source.
	 */
	public static List<Integer> getRandomIntegerSequence(int maxEx, int amount, boolean unique, RandomSource rnd) {
		if (amount > maxEx && unique)
			throw new IllegalArgumentException("getRandomIntegerSequence unique requires amount <= maxEx");
		List<Integer> all = NFUContainerStatics.intRangeList(0, amount, 1);
		List<Integer> out = new ArrayList<>(amount * 2);
		for (int i = 0; i < amount; ++i) {
			int pickedIndex = rnd.nextInt(out.size());
			out.add(all.get(pickedIndex));
			if (unique)
				all.remove(pickedIndex);
		}
		return out;
	}

	/**
	 * Randomly pick a given amount of integers from the range [0, maxEx) successively and fill into a list.
	 * @param maxEx Upper bound of the integers (excluding, i.e. range = [0, maxEx))
	 * @param amount Amount of picked integers.
	 * @param unique If true, each element of the output sequence will be unique.
	 */
	public static List<Integer> getRandomIntegerSequence(int maxEx, int amount, boolean unique) {
		return getRandomIntegerSequence(maxEx, amount, unique, RND);
	}

	/**
	 * Convert a number to Roman representation. 1-3999 supported.
	 */
	public static String intToRoman(int num) {
		final int[] vals = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

		if (num < 1 || num > 3999) {
			throw new IllegalArgumentException("Supports only 1-3999.");
		}
		StringBuilder roman = new StringBuilder();
		for (int i = 0; i < vals.length; i++) {
			while (num >= vals[i]) {
				roman.append(symbols[i]);
				num -= vals[i];
			}
		}
		return roman.toString();
	}

}
