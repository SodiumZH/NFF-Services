package net.sodiumstudio.nautils.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface NaSerializableCapability<T> extends INBTSerializable<CompoundTag>
{
	
	public T getOwner();
	
	public CompoundTag getTag();
	
}
