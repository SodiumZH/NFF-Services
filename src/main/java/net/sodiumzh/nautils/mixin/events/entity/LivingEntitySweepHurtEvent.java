package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumzh.nautils.events.NaUtilsLivingEvent;

/**
 * Fired when living entity is taking sweep hurt.
 */
@Cancelable
public class LivingEntitySweepHurtEvent extends NaUtilsLivingEvent<LivingEntity>
{

	public final Player attacker;
	
	public LivingEntitySweepHurtEvent(LivingEntity entity, Player attacker)
	{
		super(entity);
		this.attacker = attacker;
	}
}
