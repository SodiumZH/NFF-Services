package net.sodiumzh.nautils.math;

import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;

public class HtmlColors
{
	public static enum Colors {
		ALICE_BLUE("alice_blue", LinearColor.fromCode(0xf0f8ff)),
		ANTIQUE_WHITE("antique_white", LinearColor.fromCode(0xfaebd7)),
		AQUA("aqua", LinearColor.fromCode(0x00ffff)),
		AQUA_MARINE("aqua_marine", LinearColor.fromCode(0x7fffd4)),
		AZURE("azure", LinearColor.fromCode(0xf0ffff)),
		BEIGE("beige", LinearColor.fromCode(0xf5f5dc)),
		BISQUE("bisque", LinearColor.fromCode(0xffe4c4)),
		BLACK("black", LinearColor.fromCode(0x000000)),
		BLANCHED_ALMOND("blanched_almond", LinearColor.fromCode(0xffebcd)),
		BLUE("blue", LinearColor.fromCode(0x0000ff)),
		BLUE_VIOLET("blue_violet", LinearColor.fromCode(0x8a2be2)),
		BROWN("brown", LinearColor.fromCode(0xa52a2a)),
		BURLY_WOOD("burly_wood", LinearColor.fromCode(0xdeb887)),
		CADET_BLUE("cadet_blue", LinearColor.fromCode(0x5f9ea0)),
		CHARTREUSE("chartreuse", LinearColor.fromCode(0x7fff00)),
		CHOCOLATE("chocolate", LinearColor.fromCode(0xd2691e)),
		CORAL("coral", LinearColor.fromCode(0xff7f50)),
		CORNFLOWER_BLUE("cornflower_blue", LinearColor.fromCode(0x6495ed)),
		CORNSILK("cornsilk", LinearColor.fromCode(0xfff8dc)),
		CRIMSON("crimson", LinearColor.fromCode(0xdc143c)),
		CYAN("cyan", LinearColor.fromCode(0x00ffff)),
		DARK_BLUE("dark_blue", LinearColor.fromCode(0x00008b)),
		DARK_CYAN("dark_cyan", LinearColor.fromCode(0x008b8b)),
		DARK_GOLDEN_ROD("dark_golden_rod", LinearColor.fromCode(0xb8860b)),
		DARK_GRAY("dark_gray", LinearColor.fromCode(0xa9a9a9)),
		DARK_GREEN("dark_green", LinearColor.fromCode(0x006400)),
		DARK_KHAKI("dark_khaki", LinearColor.fromCode(0xbdb76b)),
		DARK_MAGENTA("dark_magenta", LinearColor.fromCode(0x8b008b)),
		DARK_OLIVE_GREEN("dark_olive_green", LinearColor.fromCode(0x556b2f)),
		DARK_ORANGE("dark_orange", LinearColor.fromCode(0xff8c00)),
		DARK_ORCHID("dark_orchid", LinearColor.fromCode(0x9932cc)),
		DARK_RED("dark_red", LinearColor.fromCode(0x8b0000)),
		DARK_SALMON("dark_salmon", LinearColor.fromCode(0xe9967a)),
		DARK_SEA_GREEN("dark_sea_green", LinearColor.fromCode(0x8fbc8f)),
		DARK_SLATE_BLUE("dark_slate_blue", LinearColor.fromCode(0x483d8b)),
		DARK_SLATE_GRAY("dark_slate_gray", LinearColor.fromCode(0x2f4f4f)),
		DARK_TURQUOISE("dark_turquoise", LinearColor.fromCode(0x00ced1)),
		DARK_VIOLET("dark_violet", LinearColor.fromCode(0x9400d3)),
		DEEP_PINK("deep_pink", LinearColor.fromCode(0xff1493)),
		DEEP_SKY_BLUE("deep_sky_blue", LinearColor.fromCode(0x00bfff)),
		DIM_GRAY("dim_gray", LinearColor.fromCode(0x696969)),
		DODGER_BLUE("dodger_blue", LinearColor.fromCode(0x1e90ff)),
		FELDSPAR("feldspar", LinearColor.fromCode(0xd19275)),
		FIRE_BRICK("fire_brick", LinearColor.fromCode(0xb22222)),
		FLORAL_WHITE("floral_white", LinearColor.fromCode(0xfffaf0)),
		FOREST_GREEN("forest_green", LinearColor.fromCode(0x228b22)),
		FUCHSIA("fuchsia", LinearColor.fromCode(0xff00ff)),
		GAINSBORO("gainsboro", LinearColor.fromCode(0xdcdcdc)),
		GHOST_WHITE("ghost_white", LinearColor.fromCode(0xf8f8ff)),
		GOLD("gold", LinearColor.fromCode(0xffd700)),
		GOLDEN_ROD("golden_rod", LinearColor.fromCode(0xdaa520)),
		GRAY("gray", LinearColor.fromCode(0x808080)),
		GREEN("green", LinearColor.fromCode(0x008000)),
		GREEN_YELLOW("green_yellow", LinearColor.fromCode(0xadff2f)),
		HONEY_DEW("honey_dew", LinearColor.fromCode(0xf0fff0)),
		HOT_PINK("hot_pink", LinearColor.fromCode(0xff69b4)),
		INDIAN_RED("indian_red", LinearColor.fromCode(0xcd5c5c)),
		INDIGO("indigo", LinearColor.fromCode(0x4b0082)),
		IVORY("ivory", LinearColor.fromCode(0xfffff0)),
		KHAKI("khaki", LinearColor.fromCode(0xf0e68c)),
		LAVENDER("lavender", LinearColor.fromCode(0xe6e6fa)),
		LAVENDER_BLUSH("lavender_blush", LinearColor.fromCode(0xfff0f5)),
		LAWN_GREEN("lawn_green", LinearColor.fromCode(0x7cfc00)),
		LEMON_CHIFFON("lemon_chiffon", LinearColor.fromCode(0xfffacd)),
		LIGHT_BLUE("light_blue", LinearColor.fromCode(0xadd8e6)),
		LIGHT_CORAL("light_coral", LinearColor.fromCode(0xf08080)),
		LIGHT_CYAN("light_cyan", LinearColor.fromCode(0xe0ffff)),
		LIGHT_GOLDEN_ROD_YELLOW("light_golden_rod_yellow", LinearColor.fromCode(0xfafad2)),
		LIGHT_GREY("light_grey", LinearColor.fromCode(0xd3d3d3)),
		LIGHT_GREEN("light_green", LinearColor.fromCode(0x90ee90)),
		LIGHT_PINK("light_pink", LinearColor.fromCode(0xffb6c1)),
		LIGHT_SALMON("light_salmon", LinearColor.fromCode(0xffa07a)),
		LIGHT_SEA_GREEN("light_sea_green", LinearColor.fromCode(0x20b2aa)),
		LIGHT_SKY_BLUE("light_sky_blue", LinearColor.fromCode(0x87cefa)),
		LIGHT_SLATE_BLUE("light_slate_blue", LinearColor.fromCode(0x8470ff)),
		LIGHT_SLATE_GRAY("light_slate_gray", LinearColor.fromCode(0x778899)),
		LIGHT_STEEL_BLUE("light_steel_blue", LinearColor.fromCode(0xb0c4de)),
		LIGHT_YELLOW("light_yellow", LinearColor.fromCode(0xffffe0)),
		LIME("lime", LinearColor.fromCode(0x00ff00)),
		LIME_GREEN("lime_green", LinearColor.fromCode(0x32cd32)),
		LINEN("linen", LinearColor.fromCode(0xfaf0e6)),
		MAGENTA("magenta", LinearColor.fromCode(0xff00ff)),
		MAROON("maroon", LinearColor.fromCode(0x800000)),
		MEDIUM_AQUA_MARINE("medium_aqua_marine", LinearColor.fromCode(0x66cdaa)),
		MEDIUM_BLUE("medium_blue", LinearColor.fromCode(0x0000cd)),
		MEDIUM_ORCHID("medium_orchid", LinearColor.fromCode(0xba55d3)),
		MEDIUM_PURPLE("medium_purple", LinearColor.fromCode(0x9370d8)),
		MEDIUM_SEA_GREEN("medium_sea_green", LinearColor.fromCode(0x3cb371)),
		MEDIUM_SLATE_BLUE("medium_slate_blue", LinearColor.fromCode(0x7b68ee)),
		MEDIUM_SPRING_GREEN("medium_spring_green", LinearColor.fromCode(0x00fa9a)),
		MEDIUM_TURQUOISE("medium_turquoise", LinearColor.fromCode(0x48d1cc)),
		MEDIUM_VIOLET_RED("medium_violet_red", LinearColor.fromCode(0xc71585)),
		MIDNIGHT_BLUE("midnight_blue", LinearColor.fromCode(0x191970)),
		MINT_CREAM("mint_cream", LinearColor.fromCode(0xf5fffa)),
		MISTY_ROSE("misty_rose", LinearColor.fromCode(0xffe4e1)),
		MOCCASIN("moccasin", LinearColor.fromCode(0xffe4b5)),
		NAVAJO_WHITE("navajo_white", LinearColor.fromCode(0xffdead)),
		NAVY("navy", LinearColor.fromCode(0x000080)),
		OLD_LACE("old_lace", LinearColor.fromCode(0xfdf5e6)),
		OLIVE("olive", LinearColor.fromCode(0x808000)),
		OLIVE_DRAB("olive_drab", LinearColor.fromCode(0x6b8e23)),
		ORANGE("orange", LinearColor.fromCode(0xffa500)),
		ORANGE_RED("orange_red", LinearColor.fromCode(0xff4500)),
		ORCHID("orchid", LinearColor.fromCode(0xda70d6)),
		PALE_GOLDEN_ROD("pale_golden_rod", LinearColor.fromCode(0xece6e0)),
		PALE_GREEN("pale_green", LinearColor.fromCode(0x98fb98)),
		PALE_TURQUOISE("pale_turquoise", LinearColor.fromCode(0xafeeee)),
		PALE_VIOLET_RED("pale_violet_red", LinearColor.fromCode(0xdb7093)),
		PAPAYA_WHIP("papaya_whip", LinearColor.fromCode(0xffefd5)),
		PEACH_PUFF("peach_puff", LinearColor.fromCode(0xffdab9)),
		PERU("peru", LinearColor.fromCode(0xcd853f)),
		PINK("pink", LinearColor.fromCode(0xffc0cb)),
		PLUM("plum", LinearColor.fromCode(0xdda0dd)),
		POWDER_BLUE("powder_blue", LinearColor.fromCode(0xb0e0e6)),
		PURPLE("purple", LinearColor.fromCode(0x800080)),
		RED("red", LinearColor.fromCode(0xff0000)),
		ROSY_BROWN("rosy_brown", LinearColor.fromCode(0xbc8f8f)),
		ROYAL_BLUE("royal_blue", LinearColor.fromCode(0x4169e1)),
		SADDLE_BROWN("saddle_brown", LinearColor.fromCode(0x8b4513)),
		SALMON("salmon", LinearColor.fromCode(0xfa8072)),
		SANDY_BROWN("sandy_brown", LinearColor.fromCode(0xf4a460)),
		SEA_GREEN("sea_green", LinearColor.fromCode(0x2e8b57)),
		SEA_SHELL("sea_shell", LinearColor.fromCode(0xfff5ee)),
		SIENNA("sienna", LinearColor.fromCode(0xa0522d)),
		SILVER("silver", LinearColor.fromCode(0xc0c0c0)),
		SKY_BLUE("sky_blue", LinearColor.fromCode(0x87ceeb)),
		SLATE_BLUE("slate_blue", LinearColor.fromCode(0x6a5acd)),
		SLATE_GRAY("slate_gray", LinearColor.fromCode(0x708090)),
		SNOW("snow", LinearColor.fromCode(0xfffafa)),
		SPRING_GREEN("spring_green", LinearColor.fromCode(0x00ff7f)),
		STEEL_BLUE("steel_blue", LinearColor.fromCode(0x4682b4)),
		TAN("tan", LinearColor.fromCode(0xd2b48c)),
		TEAL("teal", LinearColor.fromCode(0x008080)),
		THISTLE("thistle", LinearColor.fromCode(0xd8bfd8)),
		TOMATO("tomato", LinearColor.fromCode(0xff6347)),
		TURQUOISE("turquoise", LinearColor.fromCode(0x40e0d0)),
		VIOLET("violet", LinearColor.fromCode(0xee82ee)),
		VIOLET_RED("violet_red", LinearColor.fromCode(0xd02090)),
		WHEAT("wheat", LinearColor.fromCode(0xf5deb3)),
		WHITE("white", LinearColor.fromCode(0xffffff)),
		WHITE_SMOKE("white_smoke", LinearColor.fromCode(0xf5f5f5)),
		YELLOW("yellow", LinearColor.fromCode(0xffff00)),
		YELLOW_GREEN("yellow_green", LinearColor.fromCode(0x9acd32));

		private String string;
		private LinearColor color;
		private Colors(String string, LinearColor color)
		{
			this.string = string;
			this.color = color;
		}

		public String getString(){
			return string;
		}

		public LinearColor getColor(){
			return color;
		}

	}
	public static final HashMap<String, LinearColor> HTML_COLORS = NaUtilsContainerStatics.mapOf();
	static
	{
		/*HTML_COLORS.put("alice_blue", LinearColor.fromCode(0xf0f8ff));
		HTML_COLORS.put("antique_white", LinearColor.fromCode(0xfaebd7));
		HTML_COLORS.put("aqua", LinearColor.fromCode(0x00ffff));
		HTML_COLORS.put("aqua_marine", LinearColor.fromCode(0x7fffd4));
		HTML_COLORS.put("azure", LinearColor.fromCode(0xf0ffff));
		HTML_COLORS.put("beige", LinearColor.fromCode(0xf5f5dc));
		HTML_COLORS.put("bisque", LinearColor.fromCode(0xffe4c4));
		HTML_COLORS.put("black", LinearColor.fromCode(0x000000));
		HTML_COLORS.put("blanched_almond", LinearColor.fromCode(0xffebcd));
		HTML_COLORS.put("blue", LinearColor.fromCode(0x0000ff));
		HTML_COLORS.put("blue_violet", LinearColor.fromCode(0x8a2be2));
		HTML_COLORS.put("brown", LinearColor.fromCode(0xa52a2a));
		HTML_COLORS.put("burly_wood", LinearColor.fromCode(0xdeb887));
		HTML_COLORS.put("cadet_blue", LinearColor.fromCode(0x5f9ea0));
		HTML_COLORS.put("chartreuse", LinearColor.fromCode(0x7fff00));
		HTML_COLORS.put("chocolate", LinearColor.fromCode(0xd2691e));
		HTML_COLORS.put("coral", LinearColor.fromCode(0xff7f50));
		HTML_COLORS.put("cornflower_blue", LinearColor.fromCode(0x6495ed));
		HTML_COLORS.put("cornsilk", LinearColor.fromCode(0xfff8dc));
		HTML_COLORS.put("crimson", LinearColor.fromCode(0xdc143c));
		HTML_COLORS.put("cyan", LinearColor.fromCode(0x00ffff));
		HTML_COLORS.put("dark_blue", LinearColor.fromCode(0x00008b));
		HTML_COLORS.put("dark_cyan", LinearColor.fromCode(0x008b8b));
		HTML_COLORS.put("dark_golden_rod", LinearColor.fromCode(0xb8860b));
		HTML_COLORS.put("dark_gray", LinearColor.fromCode(0xa9a9a9));
		HTML_COLORS.put("dark_green", LinearColor.fromCode(0x006400));
		HTML_COLORS.put("dark_khaki", LinearColor.fromCode(0xbdb76b));
		HTML_COLORS.put("dark_magenta", LinearColor.fromCode(0x8b008b));
		HTML_COLORS.put("dark_olive_green", LinearColor.fromCode(0x556b2f));
		HTML_COLORS.put("dark_orange", LinearColor.fromCode(0xff8c00));
		HTML_COLORS.put("dark_orchid", LinearColor.fromCode(0x9932cc));
		HTML_COLORS.put("dark_red", LinearColor.fromCode(0x8b0000));
		HTML_COLORS.put("dark_salmon", LinearColor.fromCode(0xe9967a));
		HTML_COLORS.put("dark_sea_green", LinearColor.fromCode(0x8fbc8f));
		HTML_COLORS.put("dark_slate_blue", LinearColor.fromCode(0x483d8b));
		HTML_COLORS.put("dark_slate_gray", LinearColor.fromCode(0x2f4f4f));
		HTML_COLORS.put("dark_turquoise", LinearColor.fromCode(0x00ced1));
		HTML_COLORS.put("dark_violet", LinearColor.fromCode(0x9400d3));
		HTML_COLORS.put("deep_pink", LinearColor.fromCode(0xff1493));
		HTML_COLORS.put("deep_sky_blue", LinearColor.fromCode(0x00bfff));
		HTML_COLORS.put("dim_gray", LinearColor.fromCode(0x696969));
		HTML_COLORS.put("dodger_blue", LinearColor.fromCode(0x1e90ff));
		HTML_COLORS.put("feldspar", LinearColor.fromCode(0xd19275));
		HTML_COLORS.put("fire_brick", LinearColor.fromCode(0xb22222));
		HTML_COLORS.put("floral_white", LinearColor.fromCode(0xfffaf0));
		HTML_COLORS.put("forest_green", LinearColor.fromCode(0x228b22));
		HTML_COLORS.put("fuchsia", LinearColor.fromCode(0xff00ff));
		HTML_COLORS.put("gainsboro", LinearColor.fromCode(0xdcdcdc));
		HTML_COLORS.put("ghost_white", LinearColor.fromCode(0xf8f8ff));
		HTML_COLORS.put("gold", LinearColor.fromCode(0xffd700));
		HTML_COLORS.put("golden_rod", LinearColor.fromCode(0xdaa520));
		HTML_COLORS.put("gray", LinearColor.fromCode(0x808080));
		HTML_COLORS.put("green", LinearColor.fromCode(0x008000));
		HTML_COLORS.put("green_yellow", LinearColor.fromCode(0xadff2f));
		HTML_COLORS.put("honey_dew", LinearColor.fromCode(0xf0fff0));
		HTML_COLORS.put("hot_pink", LinearColor.fromCode(0xff69b4));
		HTML_COLORS.put("indian_red ", LinearColor.fromCode(0xcd5c5c));
		HTML_COLORS.put("indigo ", LinearColor.fromCode(0x4b0082));
		HTML_COLORS.put("ivory", LinearColor.fromCode(0xfffff0));
		HTML_COLORS.put("khaki", LinearColor.fromCode(0xf0e68c));
		HTML_COLORS.put("lavender", LinearColor.fromCode(0xe6e6fa));
		HTML_COLORS.put("lavender_blush", LinearColor.fromCode(0xfff0f5));
		HTML_COLORS.put("lawn_green", LinearColor.fromCode(0x7cfc00));
		HTML_COLORS.put("lemon_chiffon", LinearColor.fromCode(0xfffacd));
		HTML_COLORS.put("light_blue", LinearColor.fromCode(0xadd8e6));
		HTML_COLORS.put("light_coral", LinearColor.fromCode(0xf08080));
		HTML_COLORS.put("light_cyan", LinearColor.fromCode(0xe0ffff));
		HTML_COLORS.put("light_golden_rod_yellow", LinearColor.fromCode(0xfafad2));
		HTML_COLORS.put("light_grey", LinearColor.fromCode(0xd3d3d3));
		HTML_COLORS.put("light_green", LinearColor.fromCode(0x90ee90));
		HTML_COLORS.put("light_pink", LinearColor.fromCode(0xffb6c1));
		HTML_COLORS.put("light_salmon", LinearColor.fromCode(0xffa07a));
		HTML_COLORS.put("light_sea_green", LinearColor.fromCode(0x20b2aa));
		HTML_COLORS.put("light_sky_blue", LinearColor.fromCode(0x87cefa));
		HTML_COLORS.put("light_slate_blue", LinearColor.fromCode(0x8470ff));
		HTML_COLORS.put("light_slate_gray", LinearColor.fromCode(0x778899));
		HTML_COLORS.put("light_steel_blue", LinearColor.fromCode(0xb0c4de));
		HTML_COLORS.put("light_yellow", LinearColor.fromCode(0xffffe0));
		HTML_COLORS.put("lime", LinearColor.fromCode(0x00ff00));
		HTML_COLORS.put("lime_green", LinearColor.fromCode(0x32cd32));
		HTML_COLORS.put("linen", LinearColor.fromCode(0xfaf0e6));
		HTML_COLORS.put("magenta", LinearColor.fromCode(0xff00ff));
		HTML_COLORS.put("maroon", LinearColor.fromCode(0x800000));
		HTML_COLORS.put("medium_aqua_marine", LinearColor.fromCode(0x66cdaa));
		HTML_COLORS.put("medium_blue", LinearColor.fromCode(0x0000cd));
		HTML_COLORS.put("medium_orchid", LinearColor.fromCode(0xba55d3));
		HTML_COLORS.put("medium_purple", LinearColor.fromCode(0x9370d8));
		HTML_COLORS.put("medium_sea_green", LinearColor.fromCode(0x3cb371));
		HTML_COLORS.put("medium_slate_blue", LinearColor.fromCode(0x7b68ee));
		HTML_COLORS.put("medium_spring_green", LinearColor.fromCode(0x00fa9a));
		HTML_COLORS.put("medium_turquoise", LinearColor.fromCode(0x48d1cc));
		HTML_COLORS.put("medium_violet_red", LinearColor.fromCode(0xc71585));
		HTML_COLORS.put("midnight_blue", LinearColor.fromCode(0x191970));
		HTML_COLORS.put("mint_cream", LinearColor.fromCode(0xf5fffa));
		HTML_COLORS.put("misty_rose", LinearColor.fromCode(0xffe4e1));
		HTML_COLORS.put("moccasin", LinearColor.fromCode(0xffe4b5));
		HTML_COLORS.put("navajo_white", LinearColor.fromCode(0xffdead));
		HTML_COLORS.put("navy", LinearColor.fromCode(0x000080));
		HTML_COLORS.put("old_lace", LinearColor.fromCode(0xfdf5e6));
		HTML_COLORS.put("olive", LinearColor.fromCode(0x808000));
		HTML_COLORS.put("olive_drab", LinearColor.fromCode(0x6b8e23));
		HTML_COLORS.put("orange", LinearColor.fromCode(0xffa500));
		HTML_COLORS.put("orange_red", LinearColor.fromCode(0xff4500));
		HTML_COLORS.put("orchid", LinearColor.fromCode(0xda70d6));
		HTML_COLORS.put("pale_golden_rod", LinearColor.fromCode(0xeee8aa));
		HTML_COLORS.put("pale_green", LinearColor.fromCode(0x98fb98));
		HTML_COLORS.put("pale_turquoise", LinearColor.fromCode(0xafeeee));
		HTML_COLORS.put("pale_violet_red", LinearColor.fromCode(0xd87093));
		HTML_COLORS.put("papaya_whip", LinearColor.fromCode(0xffefd5));
		HTML_COLORS.put("peach_puff", LinearColor.fromCode(0xffdab9));
		HTML_COLORS.put("peru", LinearColor.fromCode(0xcd853f));
		HTML_COLORS.put("pink", LinearColor.fromCode(0xffc0cb));
		HTML_COLORS.put("plum", LinearColor.fromCode(0xdda0dd));
		HTML_COLORS.put("powder_blue", LinearColor.fromCode(0xb0e0e6));
		HTML_COLORS.put("purple", LinearColor.fromCode(0x800080));
		HTML_COLORS.put("red", LinearColor.fromCode(0xff0000));
		HTML_COLORS.put("rosy_brown", LinearColor.fromCode(0xbc8f8f));
		HTML_COLORS.put("royal_blue", LinearColor.fromCode(0x4169e1));
		HTML_COLORS.put("saddle_brown", LinearColor.fromCode(0x8b4513));
		HTML_COLORS.put("salmon", LinearColor.fromCode(0xfa8072));
		HTML_COLORS.put("sandy_brown", LinearColor.fromCode(0xf4a460));
		HTML_COLORS.put("sea_green", LinearColor.fromCode(0x2e8b57));
		HTML_COLORS.put("sea_shell", LinearColor.fromCode(0xfff5ee));
		HTML_COLORS.put("sienna", LinearColor.fromCode(0xa0522d));
		HTML_COLORS.put("silver", LinearColor.fromCode(0xc0c0c0));
		HTML_COLORS.put("sky_blue", LinearColor.fromCode(0x87ceeb));
		HTML_COLORS.put("slate_blue", LinearColor.fromCode(0x6a5acd));
		HTML_COLORS.put("slate_gray", LinearColor.fromCode(0x708090));
		HTML_COLORS.put("snow", LinearColor.fromCode(0xfffafa));
		HTML_COLORS.put("spring_green", LinearColor.fromCode(0x00ff7f));
		HTML_COLORS.put("steel_blue", LinearColor.fromCode(0x4682b4));
		HTML_COLORS.put("tan", LinearColor.fromCode(0xd2b48c));
		HTML_COLORS.put("teal", LinearColor.fromCode(0x008080));
		HTML_COLORS.put("thistle", LinearColor.fromCode(0xd8bfd8));
		HTML_COLORS.put("tomato", LinearColor.fromCode(0xff6347));
		HTML_COLORS.put("turquoise", LinearColor.fromCode(0x40e0d0));
		HTML_COLORS.put("violet", LinearColor.fromCode(0xee82ee));
		HTML_COLORS.put("violet_red", LinearColor.fromCode(0xd02090));
		HTML_COLORS.put("wheat", LinearColor.fromCode(0xf5deb3));
		HTML_COLORS.put("white", LinearColor.fromCode(0xffffff));
		HTML_COLORS.put("white_smoke", LinearColor.fromCode(0xf5f5f5));
		HTML_COLORS.put("yellow", LinearColor.fromCode(0xffff00));
		HTML_COLORS.put("yellow_green", LinearColor.fromCode(0x9acd32));*/
		Colors[] colors = HtmlColors.Colors.values();
		for (int i = 0; i < colors.length; ++i)
		{
			HTML_COLORS.put(colors[i].getString(), colors[i].getColor());
		}
	}
	
	public static MutableComponent getTranslationKey(String key)
	{
		return NaUtilsInfoStatics.createTranslatable("color.nautils.html." + key);
	}
	
	public static String getNearestHtmlColor(LinearColor color)
	{
		Vec3 colorv = color.toNormalized();
		return HTML_COLORS.keySet().stream().sorted(Comparator.comparingDouble((String key) -> 
		{
			return colorv.distanceToSqr(HTML_COLORS.get(key).toNormalized());
		})).toList().get(0);
	}
	
}