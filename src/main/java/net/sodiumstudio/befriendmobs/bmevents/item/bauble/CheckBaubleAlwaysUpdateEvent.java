package net.sodiumstudio.befriendmobs.bmevents.item.bauble;

import java.util.function.BiPredicate;

import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.item.baublesystem.BaubleHandler;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleEquipable;

/**
 * Fired on checking if a bauble should be updated every tick. 
 */
public class CheckBaubleAlwaysUpdateEvent extends Event
{
	public final BaubleHandler handler;
	protected OperationType operation = OperationType.OR;
	protected BiPredicate<String, IBaubleEquipable> eventCheck = (s, i) -> false;
	
	public CheckBaubleAlwaysUpdateEvent(BaubleHandler handler) 
	{
		this.handler = handler;
	}
	
	public void setOperation(OperationType type)
	{
		operation = type;
	}
	
	public OperationType getOperation()
	{
		return operation;
	}
	
	/**
	 * Add custom check. The check will be combined to other checks using OR operation.
	 * Parameter: String - bauble slot key; IBaubleEquipable - corresponding mob.
	 */
	public void addEventCheck(BiPredicate<String, IBaubleEquipable> condition)
	{
		eventCheck = eventCheck.or(condition);
	}
	
	public BiPredicate<String, IBaubleEquipable> getEventCheck()
	{
		return eventCheck;
	}
	
	public static enum OperationType
	{
		/** Ignore {@code BaubleHandler#shouldAlwaysRefresh} and use event value only */
		OVERRIDE,
		/** Use AND operation between {@code BaubleHandler#shouldAlwaysRefresh} and event value */
		AND,
		/** Use OR operation between {@code BaubleHandler#shouldAlwaysRefresh} and event value */
		OR,
		/** Use {@code BaubleHandler#shouldAlwaysRefresh} only and ignore event value */
		IGNORE
	}
}
