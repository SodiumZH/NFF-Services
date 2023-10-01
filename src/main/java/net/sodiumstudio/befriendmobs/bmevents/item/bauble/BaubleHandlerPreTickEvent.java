package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;

public class BaubleHandlerPreTickEvent extends Event
{
	public final BaubleHandler handler;
	public final IBaubleEquipable living;
	
	public BaubleHandlerPreTickEvent(BaubleHandler handler, IBaubleEquipable owner)
	{
		this.handler = handler;
		this.living = owner;
	}
}
