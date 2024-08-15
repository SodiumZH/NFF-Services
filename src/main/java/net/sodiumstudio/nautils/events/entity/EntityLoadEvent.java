package net.sodiumstudio.nautils.events.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.sodiumstudio.nautils.events.NaUtilsEntityEvent;

/**
 * Invoked before loading entity from NBT, allowing to modify the NBT before loading.
 */
public class EntityLoadEvent extends NaUtilsEntityEvent<Entity>
{
	private final CompoundTag nbt;
	
	public EntityLoadEvent(Entity entity, CompoundTag nbt)
	{
		super(entity);
		this.nbt = nbt;
	}
	
	public CompoundTag getNBT()
	{
		return nbt;
	}

}