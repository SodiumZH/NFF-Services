package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Fired when a non-living entity gets hurt only on server. 
 * <p> NOTE: This event will <b>NOT</b> be fired if it's falling out of world to prevent possible infinite falling. 
 * Instead an {@link NonLivingEntityOutOfWorldEvent} will fire.
 * <p> Note: {@link ItemEntity} will <b>NOT</b> fire this but {@link ItemEntityHurtEvent}.
 * <p> For LivingEntity, use {@link LivingHurtEvent}.
 */
public class NonLivingEntityHurtEvent extends EntityEvent {
	public final DamageSource damageSource;
	public final float amount;
	
	public NonLivingEntityHurtEvent(Entity entity, DamageSource src, float amount)
	{
		super(entity);
		this.damageSource = src;
		this.amount = amount;
	}
}
