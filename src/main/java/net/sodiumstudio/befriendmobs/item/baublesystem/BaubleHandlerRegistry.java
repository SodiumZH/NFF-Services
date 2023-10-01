package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.HashMap;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;

public class BaubleHandlerRegistry
{

	protected static final HashMap<ResourceLocation, BaubleHandler> REGISTRY = new HashMap<>();
	
	public static <T extends BaubleHandler> T create(ResourceLocation key, Supplier<T> supplier)
	{
		T instance = supplier.get();
		REGISTRY.put(key, instance);
		return instance;
	}
	
	public static <T extends BaubleHandler> T create(String modId, String name, Supplier<T> supplier)
	{
		return create(new ResourceLocation(modId, name), supplier);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BaubleHandler> T get(ResourceLocation key)
	{
		return (T) REGISTRY.get(key);
	}
	
	public static <T extends BaubleHandler> T get(String modId, String name)
	{
		return get(new ResourceLocation(modId, name));
	}
	
}
