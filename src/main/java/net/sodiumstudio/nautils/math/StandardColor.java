package net.sodiumstudio.nautils.math;

import java.util.Random;

public enum StandardColor
{
	WHITE(1d, 1d, 1d, 0),
	BLACK(0d, 0d, 0d, 1),
	GRAY(0.5d, 0.5d, 0.5d, 2),
	LIGHT_GRAY(0.75d, 0.75d, 0.75d, 3),
	RED(1d, 0d, 0d, 4),
	GREEN(0d, 1d, 0d, 5),
	BLUE(0d, 0d, 1d, 6),
	YELLOW(1d, 1d, 0d, 7),
	LIGHT_BLUE(0d, 1d, 1d, 8),
	MAGENTA(1d, 0d, 1d, 9),	
	CYAN(0d, 0.5d, 0.5d, 10),
	ORANGE(1d, 0.5d, 0d, 11),
	LIME(0.5d, 1d, 0d, 12),
	PURPLE(0.5d, 0d, 0.5d, 13),
	BROWN(0.5d, 0.25d, 0d, 14),
	PINK(1d, 0.5d, 0.5d, 15);
	
	private LinearColor color;
	private static Random rnd = new Random();
	private int id;
	
	private StandardColor(double r, double g, double b, int id)
	{
		this.color = LinearColor.fromNormalized(r, g, b);
		this.id = id;
	}
	
	public LinearColor get()
	{
		return this.color;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public static StandardColor ofId(int id)
	{
		for (int i = 0; i < StandardColor.values().length; ++i)
		{
			if (StandardColor.values()[i].getId() == id)
				return StandardColor.values()[i];
		}
		throw new IllegalArgumentException("invalid ID.");
	}
	
	public LinearColor getRandom()
	{
		return StandardColor.values()[rnd.nextInt(StandardColor.values().length)].get();
	}

}
