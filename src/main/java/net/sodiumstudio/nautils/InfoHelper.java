package net.sodiumstudio.nautils;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.sodiumstudio.nautils.info.ComponentBuilder;

public class InfoHelper
{
	/** Create a component with plain text content, equals to TextComponent in 1.18.2 */
	public static MutableComponent createText(String str)
	{
		return new TextComponent(str);
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
		return new TranslatableComponent(key, params);
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
