package net.sodiumstudio.nautils.events;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.item.ItemEvent;

/**
 * Fired when an {@link ItemEntity} falls out of world.
 */
public class ItemEntityOutOfWorldEvent extends ItemEvent
{
	public final float amount;
	
	public ItemEntityOutOfWorldEvent(ItemEntity entity, float amount)
	{
		super(entity);
		this.amount = amount;
	}
}
