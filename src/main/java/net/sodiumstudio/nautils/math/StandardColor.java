package net.sodiumstudio.nautils.math;

import java.util.Random;

import net.minecraft.util.StringRepresentable;

public enum StandardColor implements StringRepresentable
{
	WHITE("white", 1d, 1d, 1d, 0),
	BLACK("black", 0d, 0d, 0d, 1),
	GRAY("gray", 0.5d, 0.5d, 0.5d, 2),
	LIGHT_GRAY("light_gray", 0.75d, 0.75d, 0.75d, 3),
	RED("red", 1d, 0d, 0d, 4),
	GREEN("green", 0d, 1d, 0d, 5),
	BLUE("blue", 0d, 0d, 1d, 6),
	YELLOW("yellow", 1d, 1d, 0d, 7),
	LIGHT_BLUE("light_blue", 0d, 1d, 1d, 8),
	MAGENTA("magenta", 1d, 0d, 1d, 9),	
	CYAN("cyan", 0d, 0.5d, 0.5d, 10),
	ORANGE("orange", 1d, 0.5d, 0d, 11),
	LIME("lime", 0.5d, 1d, 0d, 12),
	PURPLE("purple", 0.5d, 0d, 0.5d, 13),
	BROWN("brown", 0.5d, 0.25d, 0d, 14),
	PINK("pink", 1d, 0.5d, 0.5d, 15);
	
	private LinearColor color;
	private static Random rnd = new Random();
	private int id;
	private String name;
	
	private StandardColor(String name, double r, double g, double b, int id)
	{
		this.name = name;
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

	@Override
	public String getSerializedName() 
	{
		return name;
	}

}
