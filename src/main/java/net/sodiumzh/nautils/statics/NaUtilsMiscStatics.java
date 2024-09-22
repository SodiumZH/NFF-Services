package net.sodiumzh.nautils.statics;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class NaUtilsMiscStatics {
	
	public static UUID getUUIDIfExists(Entity entity)
	{
		return entity == null ? null : entity.getUUID();
	}
	
	public static String getNameString(Entity target)
	{
		return target != null ? target.getName().getString() : "null";
	}

	public static void printToScreen(Component text, Player receiver, Entity sender)
	{
		receiver.sendMessage(text, sender.getUUID());
	}
	
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		receiver.sendMessage(NaUtilsInfoStatics.createText(text), sender.getUUID());
	}
	
	public static void printToScreen(Component text, Player receiver)
	{
		if (receiver == null)
			return;
		receiver.sendMessage(text, receiver.getUUID());
	}
	
	public static void printToScreen(String text, Player receiver)
	{
		NaUtilsMiscStatics.printToScreen(NaUtilsInfoStatics.createText(text), receiver);
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
	 * Get the value from a {@link LazyOptional}.
	 * If the value isn't present, return null.
	 */
	@Nullable
	public static <T> T getValue(LazyOptional<T> optional)
	{
		MutableObject<T> wrp = new MutableObject<>(null);
		optional.ifPresent(t -> {
			wrp.setValue(t);
		});
		return wrp.getValue();
	}
	
	/**
	 * Get the value from a {@link LazyOptional}.
	 * If the value isn't present, return a default instance defined by supplier.
	 */
	@Nonnull
	public static <T> T getValueOrDefault(LazyOptional<T> optional, NonNullSupplier<T> defaultSupplier)
	{
		T val = getValue(optional);
		if (val != null) return val;
		else return defaultSupplier.get();
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
	 * Cast an object to a given class without type check. It may be faster than {@link NaUtilsMiscStatics#cast} but throws exception when failed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T castRaw(Object obj, Class<T> clazz)
	{
		return (T)obj;
	}
	
	public static <C, T> T getValueFromCapability(Entity target, Capability<C> holder, Function<C, T> access, T fallback)
	{
		MutableObject<T> res = new MutableObject<>(fallback);
		target.getCapability(holder).ifPresent(cap -> res.setValue(access.apply(cap)));
		return res.getValue();
	}
	
	public static <C, T> T getValueFromCapability(Entity target, Capability<C> holder, Function<C, T> access)
	{
		return getValueFromCapability(target, holder, access, null);
	}
	
	/** Try an action with boolean result for given times. Once the action returns true, it will break and return true. 
	 Otherwise if the action returns all false for given times, it returns false. */
	public static boolean tryFor(int times, Supplier<Boolean> action)
	{
		if (times <= 0)
			return false;
		for (int i = 0; i < times; ++i)
		{
			boolean res = action.get();
			if (res)
				return true;
		}
		return false;
	}

}
