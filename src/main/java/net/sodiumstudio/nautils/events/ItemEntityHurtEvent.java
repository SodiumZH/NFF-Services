package net.sodiumstudio.nautils.events;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ItemEntityHurtEvent extends Event {
	public final ItemEntity entity;
	public final DamageSource damageSource;
	public final float amount;
	
	public ItemEntityHurtEvent(ItemEntity entity, DamageSource src, float amount)
	{
		this.entity = entity;
		this.damageSource = src;
		this.amount = amount;
	}
}
