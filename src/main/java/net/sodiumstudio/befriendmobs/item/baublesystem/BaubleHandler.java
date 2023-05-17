package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.util.annotation.DontOverride;

public abstract class BaubleHandler {

	// NOT IMPLEMENTED
	//public boolean shouldPopOutIfItemNotAccepted = true;
	
	/* Item types */
	/**
	 * @deprecated use mob-sensitive version instead.
	 */
	@Deprecated
	@DontOverride
	public HashSet<Item> getItemsAccepted(String key)
	{
		return new HashSet<Item>();
	}
	
	/**
	 * Get item types the bauble slot should accept. 
	 * The predicate as value is the additional condition for accepting the item. Use {@code null} for always true.
	 * If whether a slot should accept an item cannot be indicated as a map, override isAccepted() instead.
	 */
	public abstract HashMap<Item, Predicate<IBaubleHolder>> getItemsAccepted(String key, IBaubleHolder mob);
		/*HashMap<Item, Predicate<IBaubleHolder>> map = new HashMap<Item, Predicate<IBaubleHolder>>();
		// TODO: remove
		for (Item item: getItemsAccepted(key))
		{
			map.put(item, null);
		}
		
		*/

	/**
	 * @deprecated use mob sensitive version instead.
	 */
	@Deprecated
	public boolean isAccepted(Item item, String key)
	{
		BefriendMobs.LOGGER.error("BaubleHandler::isAccpeted(Item, String) is deprecated. Use mob sensitive version instead.");
		return getItemsAccepted(key).contains(item) // TODO: remove
				|| getItemsAccepted(key, null).keySet().contains(item);
	}
	
	/**
	 *  Check if an item can be added as a bauble.
	 *  By default it checks if the item is contained in getItemAccepted() return.
	 */
	public boolean isAccepted(Item item, String key, IBaubleHolder mob)
	{
		return getItemsAccepted(key).contains(item) || // TODO: remove 
				(getItemsAccepted(key, mob).keySet().contains(item) 
				&& (getItemsAccepted(key, mob).get(item) == null || getItemsAccepted(key, mob).get(item).test(mob)));
		
	}
	
	/** Check if an item can be added as a bauble (stack-sensitive version)
	 * @deprecated use mob-sensitive version instead
	 */
	@Deprecated
	@DontOverride
	public boolean isAccepted(ItemStack itemstack, String key)
	{
		return itemstack.isEmpty() ? false : isAccepted(itemstack.getItem(), key);
	}
	
	/** 
	 * Check if an item can be added as a bauble (stack-sensitive version)
	 */
	@DontOverride
	public boolean isAccepted(ItemStack itemstack, String key, IBaubleHolder mob)
	{
		return itemstack.isEmpty() ? false : isAccepted(itemstack.getItem(), key, mob);
	}
	
	/** Executed every tick
	* This is automatically ticked in events.EntityEvents and 
	* do not call it anywhere else unless you know what you're doing.
	*/
	@DontOverride
	public void tick(IBaubleHolder holder)
	{
		LivingEntity living = holder.getLiving();
		if (living.level != null && !living.level.isClientSide)
		{	
			preTick(holder);
			for (String key: holder.getBaubleSlots().keySet())
			{
				// Refresh slots
				if (this.shouldAlwaysRefresh(key, holder) || holder.hasSlotChanged(key))
				{
					// Not empty
					if (isAccepted(holder.getBaubleSlots().get(key).getItem(), key, holder))
					{
						holder.removeBaubleModifiers(key);
						this.clearBaubleEffect(key, holder);
						refreshBaubleEffect(key, holder.getBaubleSlots().get(key), holder);
					}
					// Empty
					else
					{
						holder.removeBaubleModifiers(key);
						this.clearBaubleEffect(key, holder);
					}					
				}	
			}
			postTick(holder);
			holder.saveDataCache();
		}

	}
	
	
	// Return true if an item slot should be refreshed every tick
	public boolean shouldAlwaysRefresh(String slotKey, IBaubleHolder holder)
	{
		return false;
	}
	
	// Refresh a bauble slot.
	// Define bauble effects e.g. add modifier, change owner properties etc.
	public abstract void refreshBaubleEffect(String slotKey, ItemStack bauble, IBaubleHolder owner);
	
	// Remove effects of a bauble slot. Executed when the slot is emptied.
	// Attribute modifier removal needn't to be defined here, as they will be automatically removed in tick().
	public void clearBaubleEffect(String slotKey, IBaubleHolder holder) {}
	
	// Invoked before applying bauble effects, for some additional initialization 
	public void preTick(IBaubleHolder owner) {}
	
	// Invoked after ticking bauble effects
	public void postTick(IBaubleHolder owner) {}

	/* Util */
	public static boolean isBaubleFor(ItemStack stack, IBaubleHolder holder, String key)
	{
		return holder.getBaubleHandler().isAccepted(stack, key);
	}
	
	/* IBefriendedMob inventory menu util */
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBaubleHolder slotOwner, String key)
	{
		if (slotOwner != null)
		{
			return !slot.hasItem() && isBaubleFor(stack, slotOwner, key);
		}
		else return false;
	}
	

	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, LivingEntity slotOwner, String key)
	{
		if (slotOwner != null && slotOwner instanceof IBaubleHolder holder)
		{
			return shouldBaubleSlotAccept(stack, slot, holder, key);
		}
		else return false;
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBefriendedMob slotOwner, String key)
	{
		if (slotOwner != null && slotOwner instanceof IBaubleHolder holder)
		{
			return shouldBaubleSlotAccept(stack, slot, holder, key);
		}
		else return false;
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBaubleHolder slotOwner)
	{
		return shouldBaubleSlotAccept(stack, slot, slotOwner, "null");
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, LivingEntity slotOwner)
	{
		return shouldBaubleSlotAccept(stack, slot, slotOwner, "null");
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBefriendedMob slotOwner)
	{
		return shouldBaubleSlotAccept(stack, slot, slotOwner, "null");
	}
	
	
}
