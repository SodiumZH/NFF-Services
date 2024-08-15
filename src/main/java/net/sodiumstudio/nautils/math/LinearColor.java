package net.sodiumstudio.nautils.math;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DataSerializerEntry;
import net.sodiumstudio.nautils.registries.NaUtilsEntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

/**
 * A {@code LinearColor} is a representation of a color by three 0-1 double values (i.e. normalized values) of RGB. 
 */
public class LinearColor
{

	@SuppressWarnings("unchecked")
	public static EntityDataSerializer<LinearColor> getEntityDataSerializer() {
		return (EntityDataSerializer<LinearColor>) NaUtilsEntityDataSerializers.LINEAR_COLOR.get().getSerializer();
	};

	public final double r;
	public final double g;
	public final double b;
	
	protected LinearColor(double r, double g, double b)
	{
		this.r = Mth.clamp(r, 0d, 1d);
		this.g = Mth.clamp(g, 0d, 1d);
		this.b = Mth.clamp(b, 0d, 1d);
	}
	
	/**
	 * Construct from normalized (0 - 1 double)
	 */
	public static LinearColor fromNormalized(double r, double g, double b)
	{
		return new LinearColor(r, g, b);
	}
	
	/**
	 * Construct from normalized in Vec3 (RGB = XYZ, 0 - 1 double)
	 */
	public static LinearColor fromNormalized(Vec3 normalized)
	{
		return fromNormalized(normalized.x, normalized.y, normalized.z);
	}
	
	/**
	 * Construct from RGB (0 - 255 integer)
	 */
	public static LinearColor fromRGB(int r, int g, int b)
	{
		return fromNormalized(
				(double)(r) / 255d, 
				(double)(g) / 255d, 
				(double)(b) / 255d);
	}
	
	/**
	 * Construct from RGB in Vec3i (RGB = XYZ, 0 - 255 integer)
	 */
	public static LinearColor fromRGB(Vec3i rgb)
	{
		return fromRGB(rgb.getX(), rgb.getY(), rgb.getZ());
	}
	
	/**
	 * Construct from color code integer (RRGGBB)
	 */
	public static LinearColor fromCode(int code)
	{
		int ir = (code & 0x00ff0000) >> 16;
		int ig = (code & 0x0000ff00) >> 8;
		int ib = (code & 0x000000ff); 
		return fromRGB(ir, ig, ib);
	}
	
	/**
	 * Convert to Vec3i in XYZ = RGB (0 - 255 integer) 
	 */
	public Vec3i toRGB()
	{
		return new Vec3i(
				(int) (Math.round(r * 255d)),
				(int) (Math.round(g * 255d)),
				(int) (Math.round(b * 255d))
				);
	}
	
	/**
	 * Convert to color code integer (RRGGBB)
	 */
	public int toCode()
	{
		Vec3i v = toRGB();
		return (v.getX() << 16) + (v.getY() << 8) + v.getZ();
	}
	
	public Vec3 toNormalized()
	{
		return new Vec3(r, g, b);
	}
	
	public static LinearColor lerp(LinearColor a, LinearColor b, double alpha)
	{
		return new LinearColor(
				a.r * (1 - alpha) + b.r * alpha,
				a.g * (1 - alpha) + b.g * alpha,
				a.b * (1 - alpha) + b.b * alpha);
	}
	
	public boolean isPureGray()
	{
		Vec3i v = toRGB();
		return v.getX() == v.getY() && v.getY() == v.getZ();
	}
	
	/**
	 * Get HLS = XYZ as Vec3.
	 * <p> Hue: in radian, 0-2pi; red = 0, green = 2pi/3, blue = 4pi/3
	 * <p> Lightness: 0-1
	 * <p> Saturation: 0-1
	 * @return X=H, Y=L, Z=S
	 * @see https://www.zhihu.com/question/265265004
	 * @see https://www.jianshu.com/p/366ed43c67f6
	 */
	public Vec3 toHLS()
	{
		if (isPureGray())
		{
			return new Vec3(0d, (r + g + b) / 3d, 0d);
		}
		double c = MathUtil.max(r, g, b) - MathUtil.min(r, g, b);
		double lightness = (MathUtil.max(r, g, b) + MathUtil.min(r, g, b)) / 2d;
		double saturation = 0;
		if (toCode() != 0xffffff)
			saturation = c / (1d - Math.abs(2 * lightness - 1));
		double hue = 0;
		// Get hue/60degree first
		// For red
		if (r >= g && r >= b)
			hue = (g - b) / c;
		// For green
		else if (g >= r && g >= b)
			hue = 2d + ((b - r) / c);
		else hue = 4d + ((r - g) / c);
		if (hue < 0d)
			hue += 6d;
		if (hue >= 6d)
			hue -= 6d;
		hue *= (Math.PI / 3d);
		return new Vec3(hue, lightness, saturation); 
	}

	public LinearColor getComplementary()
	{
		return new LinearColor(1d - r, 1d - g, 1d - b);
	}
	
	@Override
	public String toString()
	{
		return "LinearColor{R = " + Double.toString(r) + ", G = " + Double.toString(g) + ", B = " + Double.toString(b) + "}";
	}

}
