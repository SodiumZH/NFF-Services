package net.sodiumstudio.nautils.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.ItemEntityHurtEvent;
import net.sodiumstudio.nautils.events.ItemEntityOutOfWorldEvent;
import net.sodiumstudio.nautils.events.MobSunBurnTickEvent;
import net.sodiumstudio.nautils.events.NonLivingEntityHurtEvent;
import net.sodiumstudio.nautils.events.NonLivingEntityOutOfWorldEvent;

public class NaUtilsMixinHooks
{

	/**
	 * Invoked on entity hurt, except ItemEntity and LivingEntity.
	 * @return Whether cancelled
	 */
	public static boolean onNonLivingEntityHurt(Entity entity, DamageSource source, float amount)
	{
		if (!entity.level().isClientSide
				&& !(entity instanceof LivingEntity)
				&& !(entity instanceof ItemEntity))
		{
			if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && amount != Integer.MAX_VALUE)
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
		if (entity.level().isClientSide || entity.isRemoved()) //Forge: Fixes MC-53850
		{
			// This case will be blocked after the mixin hook invoked in vanilla code
			return false;
		}
		else
		{
			if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && amount != Integer.MAX_VALUE)
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
