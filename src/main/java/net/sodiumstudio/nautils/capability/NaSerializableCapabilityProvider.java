package net.sodiumstudio.nautils.capability;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class NaSerializableCapabilityProvider<T, C extends NaSerializableCapability<T>> implements ICapabilitySerializable<CompoundTag>
{
	
	private final C cap;
	private final Capability<C> staticCap; 
	
	public NaSerializableCapabilityProvider(T owner, Capability<C> capability, Supplier<C> capabilitySupplier)
	{
		this.cap = capabilitySupplier.get();
		this.staticCap = capability;
	}
	
	public NaSerializableCapabilityProvider(T owner, Capability<C> capability, Function<T, C> capabilitySupplier)
	{
		this.cap = capabilitySupplier.apply(owner);
		this.staticCap = capability;
	}
	
	@Override
	@Nonnull
	public <U> LazyOptional<U> getCapability(Capability<U> cap, Direction side) {
		if (cap == this.staticCap)
			return LazyOptional.of(() -> {return this.cap;}).cast();
		else return LazyOptional.empty();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return cap.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		cap.deserializeNBT(nbt);
	}

}
