package net.sodiumzh.nautils.statics;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class NaUtilsInfoStatics
{
	/** Create a component with plain text content, equals to TextComponent in 1.18.2 */
	public static MutableComponent createText(String str)
	{
		return new TextComponent(str);
	}

	/** Create a component with translatable content, equals to TranslatableComponent in 1.18.2 */
	public static MutableComponent createTranslatable(String key, Object... params)
	{
		return new TranslatableComponent(key, params);
	}
}
