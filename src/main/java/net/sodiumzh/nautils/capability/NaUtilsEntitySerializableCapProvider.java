package net.sodiumzh.nautils.capability;

import java.util.function.Supplier;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A default template Capability Provider for serializable capabilities.
 * For non-serializable caps, use {@link NaUtilsEntitySerializableCapProvider}.
 * @param <E> Entity type using this capability
 * @param <T> Capability interface.
 * @param <TG> Serializing NBT type.
 */
public class NaUtilsEntitySerializableCapProvider<E extends Entity, T extends INBTSerializable<TG>, TG extends Tag> 
	extends NaUtilsEntityCapProvider<E, T> implements ICapabilitySerializable<TG>
{

	public NaUtilsEntitySerializableCapProvider(E entity, Capability<T> holder, Supplier<T> capSupplier)
	{
		super(entity, holder, capSupplier);
	}

	@Override
	public TG serializeNBT() {
		return this.getCapInstance().serializeNBT();
	}

	@Override
	public void deserializeNBT(TG nbt) {
		this.getCapInstance().deserializeNBT(nbt);
	}

}
