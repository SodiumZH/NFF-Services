package net.sodiumzh.nfu.mixin.event.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumzh.nfu.event.NFUEntityEvent;

/**
 * Invoked after loading entity from NBT, allowing to do something right after loading.
 */
public class EntityFinalizeLoadingEvent extends NFUEntityEvent<Entity>
{
	private final CompoundTag nbt;
	
	public EntityFinalizeLoadingEvent(Entity entity, CompoundTag nbt)
	{
		super(entity);
		this.nbt = nbt;
	}
	
	public CompoundTag getNBT()
	{
		return nbt;
	}

}
