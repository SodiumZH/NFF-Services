package net.sodiumstudio.nautils.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Posted when a {@code LivingEntity} dies after {@code LivingDeathEvent}, before it <b>really</b> dies.
 * If {@code LivingDeathEvent} is cancelled, this event won't be posted.
 * <p>This event is NOT cancellable, as it indicates that the death is not cancelled.
 */
public class LivingStartDeathEvent extends NaUtilsLivingEvent<LivingEntity>
{

	public final DamageSource damageSource;
	public LivingStartDeathEvent(LivingEntity entity, DamageSource dmgSource)
	{
		super(entity);
		this.damageSource = dmgSource;
	}

}
