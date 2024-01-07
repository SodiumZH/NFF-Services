package net.sodiumstudio.nautils.containers;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.sodiumstudio.nautils.ContainerHelper;
import net.sodiumstudio.nautils.object.ObjectOrKey;
import net.sodiumstudio.nautils.object.ObjectOrSupplier;

/**
 * A {@code DynamicObjectKeyMap} is a {@link HashMap} with registered objects ({@code Item}, {@code EntityType}, etc) 
 * as keys and any object as values.
 * <p> The map key objects are {@link ObjectOrKey} and can be accessed via registries with keys. 
 * The values are {@link ObjectOrSupplier} and can be dynamically got with static map. 
 * @param <K> Raw key type.
 * @param <V> Raw value type.
 * @param <W> {@link ObjectOrKey} wrapper type.
 */
public class DynamicObjectKeyMap<K, V, W extends ObjectOrKey<K>>
{

	protected final HashMap<W, ObjectOrSupplier<V>> map = new HashMap<>();
	protected final Function<K, W> mapKeyGetterFromStatic;
	protected final Function<ResourceLocation, W> mapKeyGetterFromRegistry;
	
	protected DynamicObjectKeyMap(Function<K, W> mapKeyGetterFromStatic, Function<ResourceLocation, W> mapKeyGetterFromRegistry)
	{
		this.mapKeyGetterFromStatic = mapKeyGetterFromStatic;
		this.mapKeyGetterFromRegistry = mapKeyGetterFromRegistry;
	}

	public DynamicObjectKeyMap<K,V,W> put(K key, V value)
	{
		map.put(mapKeyGetterFromStatic.apply(key), ObjectOrSupplier.of(value));
		return this;
	}
	
	public DynamicObjectKeyMap<K,V,W> put(ResourceLocation key, V value)
	{
		map.put(mapKeyGetterFromRegistry.apply(key), ObjectOrSupplier.of(value));
		return this;
	}
	
	public DynamicObjectKeyMap<K,V,W> put(String key, V value)
	{
		map.put(mapKeyGetterFromRegistry.apply(new ResourceLocation(key)), ObjectOrSupplier.of(value));
		return this;
	}
	
	public DynamicObjectKeyMap<K,V,W> put(K key, Supplier<V> value)
	{
		map.put(mapKeyGetterFromStatic.apply(key), ObjectOrSupplier.of(value));
		return this;
	}
	
	public DynamicObjectKeyMap<K,V,W> put(ResourceLocation key, Supplier<V> value)
	{
		map.put(mapKeyGetterFromRegistry.apply(key), ObjectOrSupplier.of(value));
		return this;
	}
	
	public DynamicObjectKeyMap<K,V,W> put(String key, Supplier<V> value)
	{
		map.put(mapKeyGetterFromRegistry.apply(new ResourceLocation(key)), ObjectOrSupplier.of(value));
		return this;
	}
	
	@Nullable
	public V get(@Nonnull K rawKey)
	{
		for (W w: map.keySet())
		{
			if (w.get() == rawKey)
				return map.get(w).get();
		}
		return null;
	}
	
	public HashMap<K, V> getRawMap()
	{
		return ContainerHelper.castMap(map, k -> k.get(), v -> v.get());
	}
	
	/**
	 * Cast this to a subclass. Usually applied for return value.
	 */
	@SuppressWarnings("unchecked")
	public <T extends DynamicObjectKeyMap<K, V, W>> T cast()
	{
		try {
			return (T) this;
		} 
		catch (ClassCastException e)
		{
			LogUtils.getLogger().error("Illegal cast: only for DynamicItemKeyMap.");
			e.printStackTrace();
			throw e;
		}
	}

}

