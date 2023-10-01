package net.sodiumstudio.befriendmobs.item.baublesystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/**
 * Predicate to check if a bauble-equipping action is allowed.
 */
public class BaubleEquipingPredicate implements Predicate<BaubleEquipingInfo>
{
	protected HashMap<EntityType<? extends Mob>, Predicates> predicateMap = new HashMap<>();
	
	public BaubleEquipingPredicate()
	{
	}
	
	@Override
	public boolean test(BaubleEquipingInfo info) {
		if (!predicateMap.keySet().contains(info.mob().getType()))
			return false;
		return predicateMap.get(info.mob().getType()).test(info);
	}
	
	public static record Predicates(HashSet<String> acceptedSlots, HashSet<String> excludedSlots, Predicate<Mob> mobCheck, Predicate<ItemStack> itemStackCheck) implements Predicate<BaubleEquipingInfo>
	{
		public Predicates always()
		{
			return new Predicates(null, null, null, null);
		}
		
		@Override
		public boolean test(BaubleEquipingInfo info)
		{
			// Do mob check and item stack check first
			if (mobCheck != null && !mobCheck.test(info.mob()))
				return false;
			if (itemStackCheck != null && !itemStackCheck.test(info.baubleItemStack()))
				return false;
			// Check slots
			if (acceptedSlots != null && excludedSlots != null)
			{
				if (!acceptedSlots.contains(info.slotKey()) || excludedSlots.contains(info.slotKey()))
					return false;
			}
			else if (acceptedSlots != null && excludedSlots == null)
			{
				if (!acceptedSlots.contains(info.slotKey()))
					return false;
			}
			
			else if (acceptedSlots == null && excludedSlots != null)
			{
				if (excludedSlots.contains(info.slotKey()))
					return false;
			}
			// Appending...
			return true;
		}
	}


}
