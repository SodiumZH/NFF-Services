package net.sodiumzh.nfu.capability;

import java.util.function.Supplier;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A default template Capability Provider for serializable capabilities.
 * For non-serializable caps, use {@link NFUEntitySerializableCapProvider}.
 * @param <E> Entity type using this capability
 * @param <T> Capability interface.
 * @param <TG> Serializing NBT type.
 */
public class NFUEntitySerializableCapProvider<E extends Entity, T extends INBTSerializable<TG>, TG extends Tag>
	extends NFUEntityCapProvider<E, T> implements ICapabilitySerializable<TG>
{

	public NFUEntitySerializableCapProvider(E entity, Capability<T> holder, Supplier<? extends T> capSupplier)
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
