package net.sodiumstudio.nautils.capability;

import net.minecraft.nbt.CompoundTag;

public class NaSerializableCapabilityImpl<T> implements NaSerializableCapability<T>
{

	private CompoundTag tag;
	private final T owner;
	
	public NaSerializableCapabilityImpl(T owner)
	{
		this.owner = owner;
		this.tag = new CompoundTag();
	}
	
	@Override
	public final CompoundTag serializeNBT() {
		return tag.copy();
	}

	@Override
	public final void deserializeNBT(CompoundTag nbt) {
		this.tag = nbt.copy();
	}

	@Override
	public final T getOwner() {
		return owner;
	}

	@Override
	public final CompoundTag getTag() {
		return tag;
	}

}
