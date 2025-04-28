package net.sodiumzh.nautils.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.events.entity.ItemEntityHurtEvent;
import net.sodiumzh.nautils.mixin.events.entity.ItemEntityOutOfWorldEvent;
import net.sodiumzh.nautils.mixin.events.entity.MobSunBurnTickEvent;
import net.sodiumzh.nautils.mixin.events.entity.NonLivingEntityHurtEvent;
import net.sodiumzh.nautils.mixin.events.entity.NonLivingEntityOutOfWorldEvent;

public class NaUtilsMixinHooks
{

	/**
	 * Invoked on entity hurt, except ItemEntity and LivingEntity.
	 * @return Whether cancelled
	 */
	public static boolean onNonLivingEntityHurt(Entity entity, DamageSource source, float amount)
	{
		if (!entity.level.isClientSide
				&& !(entity instanceof LivingEntity)
				&& !(entity instanceof ItemEntity))
		{
			if (source == DamageSource.OUT_OF_WORLD && amount != Integer.MAX_VALUE)
			{
				if (MinecraftForge.EVENT_BUS.post(new NonLivingEntityOutOfWorldEvent(entity, amount)))
					return true;
			}
			else
			{
				if (MinecraftForge.EVENT_BUS.post(new NonLivingEntityHurtEvent(entity, source, amount)))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Invoked on ItemEntity hurt.
	 * @return Whether cancelled
	 */
	public static boolean onItemEntityHurt(ItemEntity entity, DamageSource source, float amount)
	{
		if (entity.level.isClientSide || entity.isRemoved()) //Forge: Fixes MC-53850
		{
			// This case will be blocked after the mixin hook invoked in vanilla code
			return false;
		}
		else
		{
			if (source == DamageSource.OUT_OF_WORLD && amount != Integer.MAX_VALUE)
			{
				if (MinecraftForge.EVENT_BUS.post(new ItemEntityOutOfWorldEvent(entity, amount)))
					return true;
			}
			else
			{
				if (MinecraftForge.EVENT_BUS.post(new ItemEntityHurtEvent(entity, source, amount)))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Invoked before a mob is calculated to be on a sun-burn tick.
	 * @return Whether cancelled. If cancelled, it will not be regarded as a sun-burn tick.
	 */
	public static boolean onMobSunBurnTick(Mob mob)
	{
		return MinecraftForge.EVENT_BUS.post(new MobSunBurnTickEvent(mob));
	}
	
}
