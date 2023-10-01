package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;

public class BaubleHandlerPostTickEvent extends Event
{
	public final BaubleHandler handler;
	public final IBaubleEquipable living;
	
	public BaubleHandlerPostTickEvent(BaubleHandler handler, IBaubleEquipable owner)
	{
		this.handler = handler;
		this.living = owner;
	}
}
