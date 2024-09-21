package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class CAttributeMonitorProvider implements ICapabilityProvider {

	public CAttributeMonitor cap;
	
	public CAttributeMonitorProvider(LivingEntity owner)
	{
		cap = new CAttributeMonitorImpl(owner);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == NFFCapRegistry.CAP_ATTRIBUTE_MONITOR)
			return LazyOptional.of(() -> {return this.cap;}).cast();
		else
			return LazyOptional.empty();
	}

	public CAttributeMonitor listen(Attribute attr)
	{
		cap.listen(attr);
		return cap;
	}
	
}
