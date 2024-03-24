package net.sodiumstudio.nautils.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.sodiumstudio.nautils.math.WithDyeColors;

public class ColoredBlocks
{
	public static final WithDyeColors<Block> WOOL_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_WOOL, "black", Blocks.BLACK_WOOL, "gray", Blocks.GRAY_WOOL, "light_gray", Blocks.LIGHT_GRAY_WOOL,
			"red", Blocks.RED_WOOL, "green", Blocks.GREEN_WOOL, "blue", Blocks.BLUE_WOOL, "yellow", Blocks.YELLOW_WOOL,
			"light_blue", Blocks.LIGHT_BLUE_WOOL, "magenta", Blocks.MAGENTA_WOOL, "cyan", Blocks.CYAN_WOOL, "orange", Blocks.ORANGE_WOOL, 
			"lime", Blocks.LIME_WOOL, "purple", Blocks.PURPLE_WOOL, "brown", Blocks.BROWN_WOOL, "pink", Blocks.PINK_WOOL);
	
	public static final WithDyeColors<Block> CARPET_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_CARPET, "black", Blocks.BLACK_CARPET, "gray", Blocks.GRAY_CARPET, "light_gray", Blocks.LIGHT_GRAY_CARPET,
			"red", Blocks.RED_CARPET, "green", Blocks.GREEN_CARPET, "blue", Blocks.BLUE_CARPET, "yellow", Blocks.YELLOW_CARPET,
			"light_blue", Blocks.LIGHT_BLUE_CARPET, "magenta", Blocks.MAGENTA_CARPET, "cyan", Blocks.CYAN_CARPET, "orange", Blocks.ORANGE_CARPET, 
			"lime", Blocks.LIME_CARPET, "purple", Blocks.PURPLE_CARPET, "brown", Blocks.BROWN_CARPET, "pink", Blocks.PINK_CARPET);
	
	public static final WithDyeColors<Block> BANNER_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_BANNER, "black", Blocks.BLACK_BANNER, "gray", Blocks.GRAY_BANNER, "light_gray", Blocks.LIGHT_GRAY_BANNER,
			"red", Blocks.RED_BANNER, "green", Blocks.GREEN_BANNER, "blue", Blocks.BLUE_BANNER, "yellow", Blocks.YELLOW_BANNER,
			"light_blue", Blocks.LIGHT_BLUE_BANNER, "magenta", Blocks.MAGENTA_BANNER, "cyan", Blocks.CYAN_BANNER, "orange", Blocks.ORANGE_BANNER, 
			"lime", Blocks.LIME_BANNER, "purple", Blocks.PURPLE_BANNER, "brown", Blocks.BROWN_BANNER, "pink", Blocks.PINK_BANNER);
	
	public static final WithDyeColors<Block> CANDLE_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_CANDLE, "black", Blocks.BLACK_CANDLE, "gray", Blocks.GRAY_CANDLE, "light_gray", Blocks.LIGHT_GRAY_CANDLE,
			"red", Blocks.RED_CANDLE, "green", Blocks.GREEN_CANDLE, "blue", Blocks.BLUE_CANDLE, "yellow", Blocks.YELLOW_CANDLE,
			"light_blue", Blocks.LIGHT_BLUE_CANDLE, "magenta", Blocks.MAGENTA_CANDLE, "cyan", Blocks.CYAN_CANDLE, "orange", Blocks.ORANGE_CANDLE, 
			"lime", Blocks.LIME_CANDLE, "purple", Blocks.PURPLE_CANDLE, "brown", Blocks.BROWN_CANDLE, "pink", Blocks.PINK_CANDLE);
	
	public static final WithDyeColors<Block> TERRACOTTA_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_TERRACOTTA, "black", Blocks.BLACK_TERRACOTTA, "gray", Blocks.GRAY_TERRACOTTA, "light_gray", Blocks.LIGHT_GRAY_TERRACOTTA,
			"red", Blocks.RED_TERRACOTTA, "green", Blocks.GREEN_TERRACOTTA, "blue", Blocks.BLUE_TERRACOTTA, "yellow", Blocks.YELLOW_TERRACOTTA, 
			"light_blue", Blocks.LIGHT_BLUE_TERRACOTTA, "magenta", Blocks.MAGENTA_TERRACOTTA, "cyan", Blocks.CYAN_TERRACOTTA, "orange", Blocks.ORANGE_TERRACOTTA,
			"lime", Blocks.LIME_TERRACOTTA, "purple", Blocks.PURPLE_TERRACOTTA, "brown", Blocks.BROWN_TERRACOTTA, "pink", Blocks.PINK_TERRACOTTA);

	public static final WithDyeColors<Block> GLAZED_TERRACOTTA_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_GLAZED_TERRACOTTA, "black", Blocks.BLACK_GLAZED_TERRACOTTA, "gray", Blocks.GRAY_GLAZED_TERRACOTTA, "light_gray", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
			"red", Blocks.RED_GLAZED_TERRACOTTA, "green", Blocks.GREEN_GLAZED_TERRACOTTA, "blue", Blocks.BLUE_GLAZED_TERRACOTTA, "yellow", Blocks.YELLOW_GLAZED_TERRACOTTA,
			"light_blue", Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, "magenta", Blocks.MAGENTA_GLAZED_TERRACOTTA, "cyan", Blocks.CYAN_GLAZED_TERRACOTTA, "orange", Blocks.ORANGE_GLAZED_TERRACOTTA, 
			"lime", Blocks.LIME_GLAZED_TERRACOTTA, "purple", Blocks.PURPLE_GLAZED_TERRACOTTA, "brown", Blocks.BROWN_GLAZED_TERRACOTTA, "pink", Blocks.PINK_GLAZED_TERRACOTTA);

	public static final WithDyeColors<Block> STAINED_GLASS_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_STAINED_GLASS, "black", Blocks.BLACK_STAINED_GLASS, "gray", Blocks.GRAY_STAINED_GLASS, "light_gray", Blocks.LIGHT_GRAY_STAINED_GLASS,
			"red", Blocks.RED_STAINED_GLASS, "green", Blocks.GREEN_STAINED_GLASS, "blue", Blocks.BLUE_STAINED_GLASS, "yellow", Blocks.YELLOW_STAINED_GLASS,
			"light_blue", Blocks.LIGHT_BLUE_STAINED_GLASS, "magenta", Blocks.MAGENTA_STAINED_GLASS, "cyan", Blocks.CYAN_STAINED_GLASS, "orange", Blocks.ORANGE_STAINED_GLASS, 
			"lime", Blocks.LIME_STAINED_GLASS, "purple", Blocks.PURPLE_STAINED_GLASS, "brown", Blocks.BROWN_STAINED_GLASS, "pink", Blocks.PINK_STAINED_GLASS);
	
	public static final WithDyeColors<Block> STAINED_GLASS_PANE_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_STAINED_GLASS_PANE, "black", Blocks.BLACK_STAINED_GLASS_PANE, "gray", Blocks.GRAY_STAINED_GLASS_PANE, "light_gray", Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
			"red", Blocks.RED_STAINED_GLASS_PANE, "green", Blocks.GREEN_STAINED_GLASS_PANE, "blue", Blocks.BLUE_STAINED_GLASS_PANE, "yellow", Blocks.YELLOW_STAINED_GLASS_PANE,
			"light_blue", Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, "magenta", Blocks.MAGENTA_STAINED_GLASS_PANE, "cyan", Blocks.CYAN_STAINED_GLASS_PANE, "orange", Blocks.ORANGE_STAINED_GLASS_PANE, 
			"lime", Blocks.LIME_STAINED_GLASS_PANE, "purple", Blocks.PURPLE_STAINED_GLASS_PANE, "brown", Blocks.BROWN_STAINED_GLASS_PANE, "pink", Blocks.PINK_STAINED_GLASS_PANE);
	
	public static final WithDyeColors<Block> SHULKER_BOX_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_SHULKER_BOX, "black", Blocks.BLACK_SHULKER_BOX, "gray", Blocks.GRAY_SHULKER_BOX, "light_gray", Blocks.LIGHT_GRAY_SHULKER_BOX,
			"red", Blocks.RED_SHULKER_BOX, "green", Blocks.GREEN_SHULKER_BOX, "blue", Blocks.BLUE_SHULKER_BOX, "yellow", Blocks.YELLOW_SHULKER_BOX,
			"light_blue", Blocks.LIGHT_BLUE_SHULKER_BOX, "magenta", Blocks.MAGENTA_SHULKER_BOX, "cyan", Blocks.CYAN_SHULKER_BOX, "orange", Blocks.ORANGE_SHULKER_BOX, 
			"lime", Blocks.LIME_SHULKER_BOX, "purple", Blocks.PURPLE_SHULKER_BOX, "brown", Blocks.BROWN_SHULKER_BOX, "pink", Blocks.PINK_SHULKER_BOX);
	
	public static final WithDyeColors<Block> BED_BLOCKS = new WithDyeColors<>(
			"white", Blocks.WHITE_BED, "black", Blocks.BLACK_BED, "gray", Blocks.GRAY_BED, "light_gray", Blocks.LIGHT_GRAY_BED,
			"red", Blocks.RED_BED, "green", Blocks.GREEN_BED, "blue", Blocks.BLUE_BED, "yellow", Blocks.YELLOW_BED,
			"light_blue", Blocks.LIGHT_BLUE_BED, "magenta", Blocks.MAGENTA_BED, "cyan", Blocks.CYAN_BED, "orange", Blocks.ORANGE_BED, 
			"lime", Blocks.LIME_BED, "purple", Blocks.PURPLE_BED, "brown", Blocks.BROWN_BED, "pink", Blocks.PINK_BED);
}
