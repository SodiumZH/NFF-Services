package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nfu.capability.SerializableCapabilityProvider;

import java.util.function.Supplier;

public class CNFFTamedProvider extends SerializableCapabilityProvider<CompoundTag, CNFFTamed>
{

	public CNFFTamedProvider(Supplier<CNFFTamed> capabilitySupplier, Capability<CNFFTamed> holder)
	{
		super(capabilitySupplier, holder);
		// TODO Auto-generated constructor stub
	}

}
