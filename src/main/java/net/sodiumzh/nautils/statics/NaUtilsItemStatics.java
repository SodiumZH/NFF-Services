package net.sodiumzh.nautils.statics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

public class NaUtilsItemStatics
{
	
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
	 * Check if an ItemStack is instance of a given item, from item's registry key.
	 */
	public static boolean is(ItemStack stack, ResourceLocation registryKey)
	{
		return stack.is(ForgeRegistries.ITEMS.getValue(registryKey));
	}
	
	/**
	 * Check if an ItemStack is instance of a given item, from item's registry key string with "domain:name" format.
	 */
	public static boolean is(ItemStack stack, String registryKey)
	{
		return is(stack, new ResourceLocation(registryKey));
	}
	
	/**
	 * Check if an ItemStack is instance of a given item, from item's registry key, domain and name.
	 */
	public static boolean is(ItemStack stack, String domain, String name)
	{
		return is(stack, new ResourceLocation(domain, name));
	}
	
	/**
	 * Get item stack enchantment level.
	 * For syncing API among different MC versions
	 */
	public static int getItemEnchantmentLevel(ItemStack stack, Enchantment enchantment)
	{
		return stack.getEnchantmentLevel(enchantment);
	}
	
	public static void giveOrDropDefault(Player player, Item item)
	{
		if (!player.addItem(item.getDefaultInstance()))
			player.spawnAtLocation(item.getDefaultInstance());
	}
	
	public static void giveOrDrop(Player player, ItemStack stack)
	{
		while (!stack.isEmpty())
		{
			ItemStack stack1 = stack.copy();
			stack1.setCount(1);
			if (!player.addItem(stack1))
				break;
			else stack.shrink(1);
		}
		if (!stack.isEmpty())
		{
			player.spawnAtLocation(stack);
		}
	}
	
	/**
	 * Get enchantment level. This is for unifying 1.18 and 1.20 API.
	 */
	public int getItemEnchantmentLevel(Enchantment enc, ItemStack stack)
	{
		return stack.getEnchantmentLevel(enc);
	}
	
	
}
