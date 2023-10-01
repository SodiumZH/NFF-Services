package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;

public class RefreshBaubleEffectEvent extends Event
{
	public final BaubleHandler handler;
	public final String slotKey;
	public final ItemStack baubleItemStack;
	public final IBaubleEquipable mob;
	
	public RefreshBaubleEffectEvent(BaubleHandler handler, String slotKey, IBaubleEquipable mob)
	{
		this.handler = handler;
		this.slotKey = slotKey;
		this.baubleItemStack = mob.getBaubleSlots().get(slotKey);
		this.mob = mob;
	}
}
