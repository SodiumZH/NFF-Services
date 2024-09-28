package net.sodiumzh.nff.services.entity.capability.wrapper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sodiumzh.nff.services.entity.capability.CAttributeMonitor;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

/**
 * A wrapper interface of {@link CAttributeMonitor}. Living Entities implementing this interface will be automatically 
 * attached {@link CAttributeMonitor} capability.
 */
public interface IAttributeMonitor
{	
	
	public default void addAttribute(Attribute attr)
	{
		((LivingEntity)this).getCapability(NFFCapRegistry.CAP_ATTRIBUTE_MONITOR).ifPresent(cap -> cap.listen(attr));
	}
	
	public void onAttributeChange(Attribute attr, double oldVal, double newVal);
	
}
