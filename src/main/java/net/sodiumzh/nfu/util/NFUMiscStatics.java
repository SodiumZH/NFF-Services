package net.sodiumzh.nfu.util;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.mutable.MutableObject;


public class NFUMiscStatics {

	/**
	 * @deprecated use {@link NFUInfoStatics#printMessage} instead
	 */
	@Deprecated
	public static void printToScreen(Component text, Player receiver, Entity sender)
	{
		printToScreen(text, receiver);
	}

	/**
	 * @deprecated use {@link NFUInfoStatics#printMessage} instead
	 */
	@Deprecated
	public static void printToScreen(String text, Player receiver, Entity sender)
	{
		printToScreen(text, receiver);
	}

	/**
	 * @deprecated use {@link NFUInfoStatics#printMessage} instead
	 */
	@Deprecated
	public static void printToScreen(Component text, Player receiver)
	{
		if (receiver == null)
			return;
		receiver.sendSystemMessage(text);
	}

	/**
	 * @deprecated use {@link NFUInfoStatics#printMessage} instead
	 */
	@Deprecated
	public static void printToScreen(String text, Player receiver)
	{
		NFUMiscStatics.printToScreen(NFUInfoStatics.createText(text), receiver);
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
	 * @deprecated Use orElse instead.
	 */
	@Nullable
	@Deprecated
	public static <T> T getValue(LazyOptional<T> optional)
	{
		return optional.orElse(null);
	}
	
	/**
	 * Get the value from a {@link LazyOptional}.
	 * If the value isn't present, return a default instance defined by supplier.
	 * @deprecated Use orElse instead.
	 */
	@Nonnull
	@Deprecated
	public static <T> T getValueOrDefault(LazyOptional<T> optional, NonNullSupplier<T> defaultSupplier)
	{
		return optional.orElseGet(defaultSupplier);
	}

	/**
	 * @deprecated Use Optional operations instead
	 */
	@Deprecated
	public static <T> T nullThen(T test, T forNull)
	{
		return Optional.ofNullable(test).orElse(forNull);
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
	 * Cast an object to a given class without type check. It may be faster than {@link NFUMiscStatics#cast} but throws exception when failed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T castRaw(Object obj, Class<T> clazz)
	{
		return (T)obj;
	}

	/**
	 * @deprecated Use Optional operations instead
	 */
	@Deprecated
	public static <C, T> T getValueFromCapability(Entity target, Capability<C> holder, Function<C, T> access, T fallback)
	{
		return target.getCapability(holder).map(access::apply).orElse(fallback);
	}

	/**
	 * @deprecated Use Optional operations instead
	 */
	@Deprecated
	public static <C, T> T getValueFromCapability(Entity target, Capability<C> holder, Function<C, T> access)
	{
		return getValueFromCapability(target, holder, access, null);
	}
	
	/** Try an action with boolean result for given times. Once the action returns true, it will break and return true. 
	 Otherwise if the action returns all false for given times, it returns false. */
	public static boolean tryFor(int times, Supplier<Boolean> action) {
		if (times <= 0)
			return false;
		for (int i = 0; i < times; ++i) {
			boolean res = action.get();
			if (res)
				return true;
		}
		return false;
	}
	/**
	 * Get entry from Forge registry if present, otherwise return empty instead of registry default value.
	 */
	public static <T> Optional<T> getEntryOptional(IForgeRegistry<T> reg, ResourceLocation key)
	{
		if (reg.containsKey(key)) return Optional.ofNullable(reg.getValue(key));
		else return Optional.empty();
	}

	/**
	 * Convert a string to UUID if it's valid, or empty if it's not.
	 * <p>Note: Use this method only when the string <i>is expected to be valid</i> for preventing exceptions. It's
	 * implemented by exception catching and may cause resource waste when handling large amount of invalid inputs.
	 */
	public static Optional<UUID> toOptionalUUID(@Nullable String strRepresentation) {
		try {
			if (strRepresentation == null) return Optional.empty();
			return Optional.of(UUID.fromString(strRepresentation));
		} catch (RuntimeException e) {
			return Optional.empty();
		}
	}
}
