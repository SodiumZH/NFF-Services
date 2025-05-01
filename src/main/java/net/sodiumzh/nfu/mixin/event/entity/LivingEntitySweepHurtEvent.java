package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Fired when living entity is taking sweep hurt.
 */
@Cancelable
public class LivingEntitySweepHurtEvent extends NFULivingEvent<LivingEntity>
{

	public final Player attacker;
	
	public LivingEntitySweepHurtEvent(LivingEntity entity, Player attacker)
	{
		super(entity);
		this.attacker = attacker;
	}
}
