package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.sodiumzh.nfu.capability.NFUEntitySerializableCapProvider;

import java.util.function.Supplier;

/**
 * Use {@link NFUEntitySerializableCapProvider}
 */
@Deprecated
public class CNFFTamableProvider extends NFUEntitySerializableCapProvider<Mob, CNFFTamable, CompoundTag> {

	public CNFFTamableProvider(Mob entity, Capability<CNFFTamable> holder, Supplier<CNFFTamable> capSupplier) {
		super(entity, holder, capSupplier);
	}
}
