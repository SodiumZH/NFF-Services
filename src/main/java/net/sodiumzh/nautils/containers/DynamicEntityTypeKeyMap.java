package net.sodiumzh.nautils.containers;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.sodiumzh.nautils.object.EntityTypeOrKey;

public class DynamicEntityTypeKeyMap<V> extends DynamicObjectKeyMap<EntityType<?>, V, EntityTypeOrKey>
{
	
	protected DynamicEntityTypeKeyMap()
	{
		super(key -> new EntityTypeOrKey(key), key -> new EntityTypeOrKey(key));
	}

	public static <V> DynamicEntityTypeKeyMap<V> create()
	{
		return new DynamicEntityTypeKeyMap<>();
	}
	
	public static <V> DynamicEntityTypeKeyMap<V> fromRaw(Map<EntityType<?>, V> raw)
	{
		DynamicEntityTypeKeyMap<V> out = create();
		for (EntityType<?> type: raw.keySet())
		{
			out.put(type, raw.get(type));
		}
		return out;
	}
	
	public static <V> DynamicEntityTypeKeyMap<V> fromRawKeys(Map<ResourceLocation, V> raw)
	{
		DynamicEntityTypeKeyMap<V> out = create();
		for (ResourceLocation key: raw.keySet())
		{
			out.put(key, raw.get(key));
		}
		return out;
	}
	
	public static <V> DynamicEntityTypeKeyMap<V> fromRawStringKeys(Map<String, V> raw)
	{
		DynamicEntityTypeKeyMap<V> out = create();
		for (String key: raw.keySet())
		{
			out.put(key, raw.get(key));
		}
		return out;
	}
}
