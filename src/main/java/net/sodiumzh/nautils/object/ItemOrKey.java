package net.sodiumzh.nautils.object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;

public class ItemOrKey extends ObjectOrKey<Item>
{

	public ItemOrKey(Item object)
	{
		super(object);
	}

	public ItemOrKey(ResourceLocation key)
	{
		super(key, ForgeRegistries.ITEMS);
	}

	public ItemOrKey(String key)
	{
		super(key, ForgeRegistries.ITEMS);
	}
	
	public ItemOrKey(String namespace, String key)
	{
		super(namespace, key, ForgeRegistries.ITEMS);
	}

	public static ItemOrKey of(Item item)
	{
		return new ItemOrKey(item);
	}
	
	public static ItemOrKey of(String key)
	{
		return new ItemOrKey(key);
	}
	
	// Utils
	
	/**
	 * Cast map keys from {@link Item} to {@link ItemOrKey}.
	 */
	public static <V> HashMap<ItemOrKey, V> fromItemKeyMap(Map<Item, V> in)
	{
		return NaUtilsContainerStatics.castMap(in, k -> ItemOrKey.of(k), v -> v, true);
	}
	
	/**
	 * Cast map values from {@link Item} to {@link ItemOrKey}.
	 */
	public static <K> HashMap<K, ItemOrKey> fromItemValueMap(Map<K, Item> in)
	{
		return NaUtilsContainerStatics.castMap(in, k -> k, v -> ItemOrKey.of(v), true); 
	}
	
	/**
	 * Cast set elements from {@link Item} to {@link ItemOrKey}.
	 */
	public static HashSet<ItemOrKey> fromItemSet(Set<Item> in)
	{
		HashSet<ItemOrKey> out = new HashSet<>();
		in.forEach(item -> out.add(ItemOrKey.of(item)));
		return out;
	}
}
