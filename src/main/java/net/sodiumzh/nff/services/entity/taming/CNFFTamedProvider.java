package net.sodiumzh.nff.services.entity.taming;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nautils.capability.SerializableCapabilityProvider;

public class CNFFTamedProvider extends SerializableCapabilityProvider<CompoundTag, CNFFTamed>
{

	public CNFFTamedProvider(Supplier<CNFFTamed> capabilitySupplier, Capability<CNFFTamed> holder)
	{
		super(capabilitySupplier, holder);
		// TODO Auto-generated constructor stub
	}

}
