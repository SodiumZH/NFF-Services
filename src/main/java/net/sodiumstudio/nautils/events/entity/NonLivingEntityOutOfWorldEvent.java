package net.sodiumstudio.nautils.events.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class NonLivingEntityOutOfWorldEvent extends EntityEvent
{
	public final float amount;
	
	public NonLivingEntityOutOfWorldEvent(Entity entity, float amount)
	{
		super(entity);
		this.amount = amount;
	}

}
