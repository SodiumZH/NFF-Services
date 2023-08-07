package net.sodiumstudio.nautils.math;

import java.util.Random;

public enum StandardColor
{
	WHITE(1d, 1d, 1d),
	BLACK(0d, 0d, 0d),
	GRAY(0.5d, 0.5d, 0.5d),
	LIGHT_GRAY(0.75d, 0.75d, 0.75d),
	RED(1d, 0d, 0d),
	GREEN(0d, 1d, 0d),
	BLUE(0d, 0d, 1d),
	YELLOW(1d, 1d, 0d),
	LIGHT_BLUE(0d, 1d, 1d),
	MAGENTA(1d, 0d, 1d),	
	CYAN(0d, 0.5d, 0.5d),
	ORANGE(1d, 0.5d, 0d),
	LIME(0.5d, 1d, 0d),
	PURPLE(0.5d, 0d, 0.5d),
	BROWN(0.5d, 0.25d, 0d),
	PINK(1d, 0.5d, 0.5d);
	
	private LinearColor color;
	private static Random rnd = new Random();
	
	private StandardColor(double r, double g, double b)
	{
		this.color = LinearColor.fromNormalized(r, g, b);
	}
	
	public LinearColor get()
	{
		return this.color;
	}
	
	public LinearColor getRandom()
	{
		return StandardColor.values()[rnd.nextInt(StandardColor.values().length)].get();
	}
}
