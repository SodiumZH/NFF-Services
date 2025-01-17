package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumzh.nautils.capability.NaUtilsEntitySerializableCapProvider;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

import java.util.function.Supplier;

/**
 * Use {@link NaUtilsEntitySerializableCapProvider}
 */
@Deprecated
public class CNFFTamableProvider extends NaUtilsEntitySerializableCapProvider<Mob, CNFFTamable, CompoundTag> {

	public CNFFTamableProvider(Mob entity, Capability<CNFFTamable> holder, Supplier<CNFFTamable> capSupplier) {
		super(entity, holder, capSupplier);
	}
}
