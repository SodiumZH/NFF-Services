package net.sodiumzh.nautils.object;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeOrKey extends ObjectOrKey<EntityType<?>>
{

	public EntityTypeOrKey(EntityType<?> object)
	{
		super(object);
	}
	
	public EntityTypeOrKey(ResourceLocation key)
	{
		super(key, ForgeRegistries.ENTITY_TYPES);
	}

	public EntityTypeOrKey(String key)
	{
		super(key, ForgeRegistries.ENTITY_TYPES);
	}
	
	public EntityTypeOrKey(String namespace, String key)
	{
		super(namespace, key, ForgeRegistries.ENTITY_TYPES);
	}
	
	public static EntityTypeOrKey of(EntityType<?> type)
	{
		return new EntityTypeOrKey(type);
	}
	
	public static EntityTypeOrKey of(String key)
	{
		return new EntityTypeOrKey(key);
	}
}
