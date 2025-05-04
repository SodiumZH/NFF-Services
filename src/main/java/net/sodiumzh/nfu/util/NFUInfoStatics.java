package net.sodiumzh.nfu.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class NFUInfoStatics
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

	/**
	 * Print system message to a player's chat box.
	 */
	public static void printMessage(Player receiver, Component msg)
	{
		if (receiver == null)
			return;
		receiver.sendMessage(msg, receiver.getUUID());
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
