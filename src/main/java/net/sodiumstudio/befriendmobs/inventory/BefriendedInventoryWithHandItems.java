package net.sodiumstudio.befriendmobs.inventory;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;

public class BefriendedInventoryWithHandItems extends BefriendedInventory
{
	public BefriendedInventoryWithHandItems(int size)
	{
		super(size);
		if (size < 2)
			throw new IllegalArgumentException("BefriendedInventoryWithHandItems must have at least 2 slots for a mob's hand item slots.");
	}
	
	public BefriendedInventoryWithHandItems(int size, IBefriendedMob owner)
	{
		super(size, owner);
	}
	
	@Override
	public void updateFromMob(Mob mob)
	{
		this.setItem(0, mob.getItemBySlot(EquipmentSlot.MAINHAND));
		this.setItem(1, mob.getItemBySlot(EquipmentSlot.OFFHAND));
		updateOwner();		
	}
	
	@Override
	public void syncToMob(Mob mob)
	{
		mob.setItemSlot(EquipmentSlot.MAINHAND, this.getItem(0));
		mob.setItemSlot(EquipmentSlot.OFFHAND, this.getItem(1));
	}
	
	public ItemStack getItemFromSlot(EquipmentSlot slot)
	{
		updateOwner();
		switch (slot)
		{
		case MAINHAND:
		{
			return this.getItem(0);
		}
		case OFFHAND:
		{
			return this.getItem(1);
		}
		default:
		{
			throw new IllegalArgumentException();
		}
		}
	}
	
}
