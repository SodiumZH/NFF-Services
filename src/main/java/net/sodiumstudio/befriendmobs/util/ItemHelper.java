package net.sodiumstudio.befriendmobs.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemHelper
{
	
	/** @deprecated use {@link ItemStack#shrink} instead
	 * 
	*/
	@Deprecated
	public static void consumeOne(ItemStack stack)
	{
		if (stack.isEmpty())
			return;
		int amount = stack.getCount();
		stack.setCount(amount - 1);
	}
	
	/**
	 * Check if player has the input item on either hand.
	 */
	public static boolean hasItemInHand(Player player, Item item)
	{
		return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
	}
	
	/**
	 * Check if player has any of the input items on either hand.
	 */
	public static boolean hasItemInHand(Player player, Item[] items)
	{
		for (Item item : items)
		{
			if (hasItemInHand(player, item))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Get item registry key as ResourceLocation.
	 */	
	public static ResourceLocation getRegistryKey(Item item)
	{
		return ForgeRegistries.ITEMS.getKey(item);
	}

	/**
	 * Get item registry key as ResourceLocation, from ItemStack.
	 */	
	public static ResourceLocation getRegistryKey(ItemStack stack)
	{
		return getRegistryKey(stack.getItem());
	}
	
	/**
	 * Get item registry key as "domain:name" formatted string.
	 */	
	public static String getRegistryKeyStr(Item item)
	{
		return getRegistryKey(item).toString();
	}

	/**
	 * Get item registry key as "domain:name" formatted string, from ItemStack.
	 */	
	public static String getRegistryKeyStr(ItemStack stack)
	{
		return getRegistryKey(stack).toString();
	}
	
	/**
	 * Get Item object from registry name. If the registry name doesn't exist, return null.
	 */
	public static Item getItem(ResourceLocation registryKey)
	{
		return ForgeRegistries.ITEMS.getValue(registryKey);
	}

	/**
	 * Get Item object from registry name. If the registry name doesn't exist, return null.
	 * Use "domain:name" format for string key.
	 */
	public static Item getItem(String registryKey)
	{
		return getItem(new ResourceLocation(registryKey));
	}
	
	/**
	 * Get Item object from registry name. If the registry name doesn't exist, return null.
	 */
	public static Item getItem(String domain, String name)
	{
		return getItem(new ResourceLocation(domain, name));
	}
	
	/**
	 * Check if an ItemStack is instance of k given item, from item's registry key.
	 */
	public static boolean is(ItemStack stack, ResourceLocation registryKey)
	{
		return stack.is(ForgeRegistries.ITEMS.getValue(registryKey));
	}
	
	public static boolean is(ItemStack stack, String registryKey)
	{
		return is(stack, new ResourceLocation(registryKey));
	}
	public static boolean is(ItemStack stack, String domain, String name)
	{
		return is(stack, new ResourceLocation(domain, name));
	}
	
}
