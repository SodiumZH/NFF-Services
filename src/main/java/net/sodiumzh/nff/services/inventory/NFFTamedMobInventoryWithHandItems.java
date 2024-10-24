package net.sodiumzh.nff.services.inventory;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

public class NFFTamedMobInventoryWithHandItems extends NFFTamedMobInventory
{
	private boolean shouldKeepArmorEmpty = false;
	
	public NFFTamedMobInventoryWithHandItems(int size)
	{
		super(size);
		if (size < 2)
			throw new IllegalArgumentException("NFFTamedMobInventoryWithHandItems must have at least 2 slots for a mob's hand item slots.");
	}
	
	public NFFTamedMobInventoryWithHandItems(int size, INFFTamed owner)
	{
		super(size, owner);
	}
	
	/**
	 * Set whether this inventory ensures mob armor slots always empty.
	 */
	public NFFTamedMobInventoryWithHandItems setShouldKeepArmorEmpty()
	{
		shouldKeepArmorEmpty = true;
		return this;
	}
	
	@Override
	public void getFromMob(Mob mob)
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
		if (shouldKeepArmorEmpty)
		{
			mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
			mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
			mob.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
			mob.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		}
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
