package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;
import net.sodiumstudio.nautils.ItemHelper;

/**
 * Fired on getting accepted items in bauble handler. Use event methods to append or removed accepted items.
 */
public class GetBaubleAcceptedItemsEvent extends Event
{
	private static Predicate<IBaubleEquipable> alwaysTrue() {return (m) -> true;}
	protected static final Supplier<Predicate<IBaubleEquipable>> ALWAYS = GetBaubleAcceptedItemsEvent::alwaysTrue;
	
	protected final HashMap<Item, Predicate<IBaubleEquipable>> accepted;
	public final BaubleHandler handler;
	// Bauble slot key of the mob.
	public final String slotKey;
	
	public GetBaubleAcceptedItemsEvent(HashMap<Item, Predicate<IBaubleEquipable>> previousMap, BaubleHandler handler, String slotKey)
	{
		accepted = previousMap;
		this.handler = handler;
		this.slotKey = slotKey;
	}
	
	/**
	 * Get current mob-predicate of item.
	 * @return Non-null predicate if exists, and null if item not found.
	 */
	@Nullable
	public Predicate<IBaubleEquipable> getPredicate(Item item)
	{
		if (!accepted.containsKey(item))
			return null;
		else return accepted.get(item) == null ? ALWAYS.get() : accepted.get(item);
	}
	
	public GetBaubleAcceptedItemsEvent append(Item item, @Nullable Predicate<IBaubleEquipable> predicate)
	{
		Predicate<IBaubleEquipable> predicateIn = predicate == null ? ALWAYS.get() : predicate;
		if (accepted.containsKey(item))
			accepted.put(item, accepted.get(item).and(predicateIn));
		else accepted.put(item, predicateIn);
		return this;
	}

	public GetBaubleAcceptedItemsEvent append(String itemRegistryKey, @Nullable Predicate<IBaubleEquipable> predicate)
	{
		Item item = ItemHelper.getItem(itemRegistryKey);
		Predicate<IBaubleEquipable> predicateIn = predicate == null ? ALWAYS.get() : predicate;
		if (item != null)
		{
			if (accepted.containsKey(item))
				accepted.put(item, accepted.get(item).and(predicateIn));
			else accepted.put(item, predicateIn);
		}
		return this;
	}
	
	public GetBaubleAcceptedItemsEvent append(Item item)
	{
		return this.append(item, ALWAYS.get());
	}
	
	public GetBaubleAcceptedItemsEvent append(String itemRegistryKey)
	{
		return this.append(itemRegistryKey, ALWAYS.get());
	}
	
	public GetBaubleAcceptedItemsEvent remove(Item item)
	{
		accepted.remove(item);
		return this;
	}
	
	public GetBaubleAcceptedItemsEvent remove(String itemRegistryKey)
	{
		Item item = ItemHelper.getItem(itemRegistryKey);
		if (item != null)
			remove(item);
		return this;
	}
	
	
}
