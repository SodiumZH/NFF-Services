package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;

public class CAttributeMonitorImpl implements CAttributeMonitor {

	protected LivingEntity living;
	
	protected HashMap<Attribute, Double> map = new HashMap<Attribute, Double>();
	
	public CAttributeMonitorImpl(LivingEntity living)
	{
		this.living = living;
	}



	@Override
	public HashMap<Attribute, Double> getListenList() {
		return map;
	}

	@Override
	public LivingEntity getEntity() {
		return living;
	}
}
