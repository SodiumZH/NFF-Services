package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;

public class ClearBaubleEffectEvent extends Event
{
	public final BaubleHandler handler;
	public final String slotKey;
	public final IBaubleEquipable mob;
	
	public ClearBaubleEffectEvent(BaubleHandler handler, String slotKey, IBaubleEquipable mob)
	{
		this.handler = handler;
		this.slotKey = slotKey;
		this.mob = mob;
	}
}
