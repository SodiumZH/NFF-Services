package net.sodiumstudio.nautils.events.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumstudio.nautils.events.NaUtilsEntityEvent;

public class EntityFinishLoadingEvent extends NaUtilsEntityEvent<Entity>
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
