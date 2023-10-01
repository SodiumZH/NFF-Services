package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.BaubleHandlerPostTickEvent;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.BaubleHandlerPreTickEvent;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.CheckBaubleAlwaysUpdateEvent;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.ClearBaubleEffectEvent;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.GetBaubleAcceptedItemsEvent;
import net.sodiumstudio.befriendmobs.bmevents.item.bauble.RefreshBaubleEffectEvent;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;
import net.sodiumstudio.nautils.ItemHelper;
import net.sodiumstudio.nautils.annotation.DontCallManually;
import net.sodiumstudio.nautils.annotation.DontOverride;

/**
 * Methods to process all baubles of a mob.
 * It should be single-instance.
 */
public abstract class BaubleHandler {
	
	public static final Predicate<IBaubleEquipable> ALWAYS = (m) -> true;
	
	public BaubleHandler() {}
	
	// NOT IMPLEMENTED
	//public boolean shouldPopOutIfItemNotAccepted = true;

	/**
	 * Get item types the bauble slot should accept. 
	 * The predicate as value is the additional condition for accepting the item. Use {@code null} for always true.
	 * If whether a slot should accept an item cannot be indicated as a map, override isAccepted() instead.
	 * <p>WARNING: DO NOT override this method. Override {@link BaubleHandler#getItemKeysAccepted} instead for item declaration.
	 */
	public final Map<Item, Predicate<IBaubleEquipable>> getItemsAccepted(String slotKey, IBaubleEquipable mob)
	{
		HashMap<Item, Predicate<IBaubleEquipable>> map = new HashMap<Item, Predicate<IBaubleEquipable>>();
		Map<String , Predicate<IBaubleEquipable>> keyMap = getItemKeysAccepted(slotKey, mob);
		for (String str: keyMap.keySet())
		{
			Item item = ItemHelper.getItem(str);
			if (item != null)
			{
				map.put(item, keyMap.get(str) == null ? ((m) -> true) : keyMap.get(str));
			}
		}
		MinecraftForge.EVENT_BUS.post(new GetBaubleAcceptedItemsEvent(map, this, slotKey));
		return map;
	}
	
	/**
	 * Get item types the bauble slot should accept. 
	 * <p> The Strings as keys are the registry name of items, using "domain:name" format.
	 * <p> The predicate as value is the additional condition for accepting the item. Use {@code null} for always true.
	 * <p> If whether a slot should accept an item cannot be indicated as a map, override isAccepted() instead.
	 * <p> WARNING: Don't call this manually because some keys may belong to other mods that may not be loaded. 
	 * Call {@link BaubleHandler#getItemAccepted} instead which will automatically ignore invalid keys.
	 * @param key The bauble slot key.
	 * @return A HashMap with item registries as keys and mob predicates as values.
	 */
	@DontCallManually
	public abstract Map<String , Predicate<IBaubleEquipable>> getItemKeysAccepted(String slotKey, IBaubleEquipable mob);
	
	/**
	 *  Check if an item can be added as a bauble.
	 *  By default it checks if the item is contained in getItemAccepted() return.
	 */
	public boolean isAccepted(Item item, String key, IBaubleEquipable mob)
	{
		return (getItemsAccepted(key, mob).keySet().contains(item) 
				&& (getItemsAccepted(key, mob).get(item) == null || getItemsAccepted(key, mob).get(item).test(mob)));		
	}
	
	/** 
	 * Check if an item can be added as a bauble (stack-sensitive version)
	 */
	@DontOverride
	public final boolean isAccepted(ItemStack itemstack, String key, IBaubleEquipable mob)
	{
		return itemstack.isEmpty() ? false : isAccepted(itemstack.getItem(), key, mob);
	}
	
	/** Executed every tick
	* This is automatically ticked in events.EntityEvents and 
	* do not call it anywhere else unless you know what you're doing.
	*/
	@DontOverride
	@DontCallManually
	public final void tick(IBaubleEquipable holder)
	{
		LivingEntity living = holder.getLiving();
		if (living.level() != null && !living.level().isClientSide)
		{	
			preTick(holder);
			MinecraftForge.EVENT_BUS.post(new BaubleHandlerPreTickEvent(this, holder));
			for (String key: holder.getBaubleSlots().keySet())
			{
				// Refresh slots
				if (this.shouldAlwaysRefreshInternal(key, holder) || holder.hasSlotChanged(key))
				{
					// Not empty
					if (isAccepted(holder.getBaubleSlots().get(key).getItem(), key, holder))
					{
						holder.removeBaubleModifiers(key);
						this.clearBaubleEffect(key, holder);
						refreshBaubleEffect(key, holder.getBaubleSlots().get(key), holder);
						MinecraftForge.EVENT_BUS.post(new RefreshBaubleEffectEvent(this, key, holder));
					}
					// Empty
					else
					{
						holder.removeBaubleModifiers(key);
						this.clearBaubleEffect(key, holder);
						MinecraftForge.EVENT_BUS.post(new ClearBaubleEffectEvent(this, key, holder));
					}					
				}	
			}
			postTick(holder);
			MinecraftForge.EVENT_BUS.post(new BaubleHandlerPostTickEvent(this, holder));
			holder.saveDataCache();
		}

	}
	
	
	/** Whether an item slot should be refreshed every tick */
	public boolean shouldAlwaysRefresh(String slotKey, IBaubleEquipable owner)
	{
		return false;
	}
	
	protected final boolean shouldAlwaysRefreshInternal(String slotKey, IBaubleEquipable owner)
	{
		CheckBaubleAlwaysUpdateEvent event = new CheckBaubleAlwaysUpdateEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getOperation())
		{
		case OVERRIDE:
		{
			return event.getEventCheck().test(slotKey, owner);
		}
		case AND:
		{
			return event.getEventCheck().test(slotKey, owner) && shouldAlwaysRefresh(slotKey, owner);
		}
		case OR:
		{
			return event.getEventCheck().test(slotKey, owner) || shouldAlwaysRefresh(slotKey, owner);
		}
		case IGNORE:
		{
			return shouldAlwaysRefresh(slotKey, owner);
		}
		default:
		{
			throw new RuntimeException();
		}
		}
	}
	
	// Refresh a bauble slot.
	// Define bauble effects e.g. add modifier, change owner properties etc.
	public abstract void refreshBaubleEffect(String slotKey, ItemStack bauble, IBaubleEquipable owner);
	
	// Remove effects of a bauble slot. Executed when the slot is emptied.
	// Attribute modifier removal needn't to be defined here, as they will be automatically removed in tick().
	public void clearBaubleEffect(String slotKey, IBaubleEquipable holder) {}
	
	// Invoked before applying bauble effects, for some additional initialization 
	public void preTick(IBaubleEquipable owner) {}
	
	// Invoked after ticking bauble effects
	public void postTick(IBaubleEquipable owner) {}

	/* Util */
	public static boolean isBaubleFor(ItemStack stack, IBaubleEquipable holder, String key)
	{
		return holder.getBaubleHandler().isAccepted(stack, key, holder);
	}
	
	/* IBefriendedMob inventory menu util */
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBaubleEquipable slotOwner, String key)
	{
		if (slotOwner != null)
		{
			return !slot.hasItem() && isBaubleFor(stack, slotOwner, key);
		}
		else return false;
	}
	

	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, LivingEntity slotOwner, String key)
	{
		if (slotOwner != null && slotOwner instanceof IBaubleEquipable holder)
		{
			return shouldBaubleSlotAccept(stack, slot, holder, key);
		}
		else return false;
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBefriendedMob slotOwner, String key)
	{
		if (slotOwner != null && slotOwner instanceof IBaubleEquipable holder)
		{
			return shouldBaubleSlotAccept(stack, slot, holder, key);
		}
		else return false;
	}
	
	public static boolean shouldBaubleSlotAccept(ItemStack stack, Slot slot, IBaubleEquipable slotOwner)
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
