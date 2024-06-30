package net.sodiumstudio.nautils.events.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired on an {@link ItemEntity} taking damage. 
 * <p>NOTE: This event will NOT be fired if it's falling out of world to prevent possible infinite falling. 
 * Instead an {@link ItemEntityOutOfWorldEvent} will fire.
 */
@Cancelable
public class ItemEntityHurtEvent extends ItemEvent {
	public final DamageSource damageSource;
	public final float amount;
	
	public ItemEntityHurtEvent(ItemEntity entity, DamageSource src, float amount)
	{
		super(entity);
		this.damageSource = src;
		this.amount = amount;
	}
}
