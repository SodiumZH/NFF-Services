package net.sodiumstudio.nautils.containers;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.sodiumstudio.nautils.object.ItemOrKey;

public class DynamicItemKeyMap<V> extends DynamicObjectKeyMap<Item, V, ItemOrKey>
{
	
	protected DynamicItemKeyMap()
	{
		super(key -> new ItemOrKey(key), key -> new ItemOrKey(key));
	}

	public static <V> DynamicItemKeyMap<V> create()
	{
		return new DynamicItemKeyMap<>();
	}
	
	public static <V> DynamicItemKeyMap<V> fromRaw(Map<Item, V> raw)
	{
		DynamicItemKeyMap<V> out = create();
		for (Item item: raw.keySet())
		{
			out.put(item, raw.get(item));
		}
		return out;
	}
	
	public static <V> DynamicItemKeyMap<V> fromRawKeys(Map<ResourceLocation, V> raw)
	{
		DynamicItemKeyMap<V> out = create();
		for (ResourceLocation key: raw.keySet())
		{
			out.put(key, raw.get(key));
		}
		return out;
	}
	
	public static <V> DynamicItemKeyMap<V> fromRawStringKeys(Map<String, V> raw)
	{
		DynamicItemKeyMap<V> out = create();
		for (String key: raw.keySet())
		{
			out.put(key, raw.get(key));
		}
		return out;
	}
}
