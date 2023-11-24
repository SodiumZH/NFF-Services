package net.sodiumstudio.nautils.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when living entity is taking sweep hurt.
 */
@Cancelable
public class LivingEntitySweepHurtEvent extends NaUtilsLivingEvent<LivingEntity>
{

	public LivingEntitySweepHurtEvent(LivingEntity entity)
	{
		super(entity);
	}
}
