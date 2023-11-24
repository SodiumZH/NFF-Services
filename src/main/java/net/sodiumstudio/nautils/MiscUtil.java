package net.sodiumstudio.nautils;

import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;


public class MiscUtil {
	
	public static UUID getUUIDIfExists(Entity entity)
	{
		return entity == null ? null : entity.getUUID();
	}
	
	public static String getNameString(Entity target)
	{
		return target != null ? target.getName().getString() : "null";
	}

	@Deprecated
	public static void printToScreen(Component text, Player receiver, Entity sender)
	{
		printToScreen(text, receiver);
	}
	
	@Deprecated
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		printToScreen(text, receiver);
	}
	
	public static void printToScreen(Component text, Player receiver)
	{
		if (receiver == null)
			return;
		receiver.sendSystemMessage(text);
	}
	
	public static void printToScreen(String text, Player receiver)
	{
		MiscUtil.printToScreen(InfoHelper.createText(text), receiver);
	}
	
	/** @deprecated Useless function */
	@Deprecated
	public static <T> boolean sameObject(T a, T b)
	{
		return a != null && a == b;
	}
	
	public static <T> boolean isIn(T test, T[] set)
	{	
		if (test == null)
			return false;
		for (T elem: set)
		{
			if (test.equals(elem))
			{
				return true;
			}
		}
		return false;
	}
	
	public static <T> boolean isIn(T test, T[] set, T nullObj)
	{
		if (test == null || test.equals(nullObj))
			return false;
		return isIn(test, set);
	}
	
	/** @deprecated Useless function */
	@Deprecated
	public static <T> boolean isIn(T test, HashSet<T> set)
	{
		for (T elem: set)
		{
			if (test.equals(elem))
			{
				return true;
			}
		}
		return false;
	}
	
	/** @deprecated Useless function */
	@Deprecated
	public static <T> boolean isIn(T test, HashSet<T> set, T nullObj)
	{
		if (test == null || test.equals(nullObj))
			return false;
		return isIn(test, set);
	}
	
	/**
	 * Get the value from a lazy optional.
	 * If the value isn't present, return null.
	 */
	@Nullable
	public static <T> T getValue(LazyOptional<T> optional)
	{
		Wrapped<T> wrp = new Wrapped<>(null);
		optional.ifPresent(t -> {
			wrp.set(t);
		});
		return wrp.get();
	}
	
	public static <T> T nullThen(T test, T forNull)
	{
		return test == null ? forNull : test;
	}
	
	/**
	 * Cast a given object to a given class. If class mismatches, return null.
	 * <p> Equivalent to {@code dynamic_cast} in C++. It will run a type check before casting. If you're sure the type matches, you can
	 * use {@code castRaw} to trivially save resource.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T cast(Object obj, Class<T> clazz)
	{
		if (clazz.isAssignableFrom(obj.getClass()))
			return (T)obj;
		else return null;
	}
	
	/**
	 * Cast an object to a given class without type check. It may be faster than {@link MiscUtil#cast} but throws exception when failed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T castRaw(Object obj, Class<T> clazz)
	{
		return (T)obj;
	}
	
	
}
