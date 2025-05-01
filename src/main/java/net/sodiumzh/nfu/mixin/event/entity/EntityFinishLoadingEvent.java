package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.event.NFUEntityEvent;

public class EntityFinishLoadingEvent extends NFUEntityEvent<Entity>
{
	private final CompoundTag nbt;
	
	public EntityFinishLoadingEvent(Entity entity, CompoundTag nbt)
	{
		super(entity);
		this.nbt = nbt;
	}
	
	public CompoundTag getNBTCopy()
	{
		return nbt.copy();
	}

}
