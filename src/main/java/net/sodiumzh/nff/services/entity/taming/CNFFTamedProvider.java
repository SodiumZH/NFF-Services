package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nfu.capability.NFUEntitySerializableCapProvider;

import java.util.function.Supplier;

public class CNFFTamedProvider extends NFUEntitySerializableCapProvider<Mob, CNFFTamed, CompoundTag>
{

	public CNFFTamedProvider(Mob entity, Capability<CNFFTamed> holder, Supplier<? extends CNFFTamed> capSupplier) {
		super(entity, holder, capSupplier);
	}
}
