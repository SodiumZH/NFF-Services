package net.sodiumzh.nautils.statics;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;

public class NaUtilsInfoStatics
{
	/** Create a component with plain text content, equals to TextComponent in 1.18.2 */
	public static MutableComponent createText(String str)
	{
		return MutableComponent.create(new LiteralContents(str));
	}

	/** Create a component with translatable content, equals to TranslatableComponent in 1.18.2 */
	public static MutableComponent createTranslatable(String key, Object... params)
	{
		return MutableComponent.create(new TranslatableContents(key, params));
	}

	/**
	 * Print system message to a player's chat box.
	 */
	public static void printMessage(Player receiver, Component msg)
	{
		if (receiver == null)
			return;
		receiver.sendSystemMessage(msg);
	}

	/**
	 * Print system message (plain text) to a player's chat box.
	 */
	public static void printMessage(Player receiver, String msg)
	{
		printMessage(receiver, createText(msg));
	}

	/**
	 * Print system message (translatable) to a player's chat box.
	 */
	public static void printMessageTranslatable(Player receiver, String key, Object... params) {
		printMessage(receiver, createTranslatable(key, params));
	}

}
