package net.sodiumstudio.nautils;

import java.util.ArrayList;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.sodiumstudio.nautils.info.ComponentBuilder;
import net.sodiumstudio.nautils.math.LinearColor;

public class InfoHelper
{
	/** Create a component with plain text content, equals to TextComponent in 1.18.2 */
	public static MutableComponent createText(String str)
	{
		return MutableComponent.create(new LiteralContents(str));
	}
	
	/**
	 * Create a component with translatable content, equals to TranslatableComponent in 1.18.2
	 * @deprecated Renamed to {@code createTranslatable}.
	 */
	@Deprecated
	public static MutableComponent createTrans(String key, Object... params)
	{
		return createTranslatable(key, params);
	}
	
	/** Create a component with translatable content, equals to TranslatableComponent in 1.18.2 */
	public static MutableComponent createTranslatable(String key, Object... params)
	{
		return MutableComponent.create(new TranslatableContents(key, params));
	}
	
	/**
	 * @deprecated Use {@link net.sodiumstudio.nautils.info.ComponentBuilder#create()} instead.
	 */
	@Deprecated
	public static ComponentBuilder builder()
	{
		return ComponentBuilder.create();
	}
	
}
