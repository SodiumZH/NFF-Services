package net.sodiumzh.nautils.mixin.events.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumzh.nautils.events.NaUtilsEntityEvent;

/**
 * Invoked after loading entity from NBT, allowing to do something right after loading.
 */
public class EntityFinalizeLoadingEvent extends NaUtilsEntityEvent<Entity>
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
