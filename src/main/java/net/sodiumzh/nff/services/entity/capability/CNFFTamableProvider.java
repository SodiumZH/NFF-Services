package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class CNFFTamableProvider implements ICapabilitySerializable<CompoundTag> {

	protected CNFFTamable capability = new CNFFTamableImpl();
		
	public CNFFTamableProvider(Mob owner)
	{
		((CNFFTamableImpl)capability).setOwner(owner);
		NFFTamingMapping.getHandler(owner).initCap(capability);
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		return capability != null ? capability.serializeNBT() : new CompoundTag();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) 
	{
		capability.deserializeNBT(nbt);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == NFFCapRegistry.CAP_BEFRIENDABLE_MOB)
			return LazyOptional.of(() -> {return this.capability;}).cast();
		else
			return LazyOptional.empty();
	}

}
