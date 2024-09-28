package net.sodiumzh.nautils.statics;

import java.util.ArrayList;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class NaUtilsTagStatics 
{
	public static boolean hasTag(Entity obj, ResourceLocation tag)
	{
		TagKey<EntityType<?>> tagKey = ForgeRegistries.ENTITIES.tags().createTagKey(tag);
		return ForgeRegistries.ENTITIES.tags().getTag(tagKey).contains(obj.getType());
	}
	
	public static boolean hasTag(Entity obj, String tag)
	{
		return hasTag(obj, new ResourceLocation(tag));
	}
	
	public static boolean hasTag(Entity obj, String domain, String tag)
	{
		return hasTag(obj, new ResourceLocation(domain, tag));
	}
	
	public static boolean hasTag(Item obj, ResourceLocation tag)
	{
		TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags().createTagKey(tag);
		return ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(obj);
	}
	
	public static boolean hasTag(Item obj, String tag)
	{
		return hasTag(obj, new ResourceLocation(tag));
	}
	
	public static boolean hasTag(Item obj, String domain, String tag)
	{
		return hasTag(obj, new ResourceLocation(domain, tag));
	}

	public static boolean hasTag(ItemStack obj, ResourceLocation tag)
	{
		if (obj.isEmpty())
			return false;
		else return hasTag(obj.getItem(), tag);
	}
	
	public static boolean hasTag(ItemStack obj, String tag)
	{
		return hasTag(obj, new ResourceLocation(tag));
	}
	
	public static boolean hasTag(ItemStack obj, String domain, String tag)
	{
		return hasTag(obj, new ResourceLocation(domain, tag));
	}
	
	
	public static boolean hasTag(Block obj, ResourceLocation tag)
	{
		TagKey<Block> tagKey = ForgeRegistries.BLOCKS.tags().createTagKey(tag);
		return ForgeRegistries.BLOCKS.tags().getTag(tagKey).contains(obj);
	}
	
	public static boolean hasTag(Block obj, String tag)
	{
		return hasTag(obj, new ResourceLocation(tag));
	}
	
	public static boolean hasTag(Block obj, String domain, String tag)
	{
		return hasTag(obj, new ResourceLocation(domain, tag));
	}

	
	public static ArrayList<EntityType<?>> getAllEntityTypesUnderTag(ResourceLocation tag)
	{
		TagKey<EntityType<?>> tagKey = ForgeRegistries.ENTITIES.tags().createTagKey(tag);
		return NaUtilsContainerStatics.iterableToList(ForgeRegistries.ENTITIES.tags().getTag(tagKey));
	}
	
	public static ArrayList<EntityType<?>> getAllEntityTypesUnderTag(String tag)
	{
		return getAllEntityTypesUnderTag(new ResourceLocation(tag));
	}
	
	public static ArrayList<EntityType<?>> getAllEntityTypesUnderTag(String domain, String tag)
	{
		return getAllEntityTypesUnderTag(new ResourceLocation(domain, tag));
	}
	
	public static ArrayList<Item> getAllItemsUnderTag(ResourceLocation tag)
	{
		TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags().createTagKey(tag);
		return NaUtilsContainerStatics.iterableToList(ForgeRegistries.ITEMS.tags().getTag(tagKey));
	}
	
	public static ArrayList<Item> getAllItemsUnderTag(String tag)
	{
		return getAllItemsUnderTag(new ResourceLocation(tag));
	}
	
	public static ArrayList<Item> getAllItemsUnderTag(String domain, String tag)
	{
		return getAllItemsUnderTag(new ResourceLocation(domain, tag));
	}
	
	public static ArrayList<Block> getAllBlocksUnderTag(ResourceLocation tag)
	{
		TagKey<Block> tagKey = ForgeRegistries.BLOCKS.tags().createTagKey(tag);
		return NaUtilsContainerStatics.iterableToList(ForgeRegistries.BLOCKS.tags().getTag(tagKey));
	}
	
	public static ArrayList<Block> getAllBlocksUnderTag(String tag)
	{
		return getAllBlocksUnderTag(new ResourceLocation(tag));
	}
	
	public static ArrayList<Block> getAllBlocksUnderTag(String domain, String tag)
	{
		return getAllBlocksUnderTag(new ResourceLocation(domain, tag));
	}
	
	public static TagKey<Block> createBlockTag(String modId, String name)
	{
		return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(modId, name));
	}
	
	public static TagKey<Item> createItemTag(String modId, String name)
	{
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(modId, name));
	}
	
	public static TagKey<EntityType<?>> createEntityTypeTag(String modId, String name)
	{
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(modId, name));
	}
}
