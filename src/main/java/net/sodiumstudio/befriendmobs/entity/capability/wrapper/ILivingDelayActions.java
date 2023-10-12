package net.sodiumstudio.befriendmobs.entity.capability.wrapper;

import net.minecraft.world.entity.LivingEntity;
import net.sodiumstudio.befriendmobs.entity.capability.CLivingEntityDelayActionHandler;
import net.sodiumstudio.befriendmobs.registry.BMCaps;

/**
 * A wrapper interface of {@link CLivingEntityDelayActionHandler}. Living Entities implementing this interface will be automatically 
 * attached {@link CLivingEntityDelayActionHandler} capability.
 */
public interface ILivingDelayActions
{
	
	public default LivingEntity getEntity()
	{
		return (LivingEntity)this;
	}
	
	public default void addAction(int delayTicks, Runnable action)
	{
		getEntity().getCapability(BMCaps.CAP_DELAY_ACTION_HANDLER).ifPresent(cap -> cap.addAction(delayTicks, action));
	}
	
}
