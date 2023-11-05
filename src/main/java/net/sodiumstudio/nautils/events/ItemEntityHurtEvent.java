package net.sodiumstudio.nautils.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.Cancelable;

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
