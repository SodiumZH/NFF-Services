package net.sodiumzh.nautils.capability;

import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A default template {@link CapabilityProvider} for serializable capabilities.
 * @param <S> Serializing NBT type.
 * @param <T> Corresponding serializable capability type.
 * @deprecated Use {@code NaUtilsEntitySerializableCapProvider} instead.
 */
@Deprecated
public class SerializableCapabilityProvider<S extends Tag, T extends INBTSerializable<S>> implements ICapabilitySerializable<S>
{
	private T cap;
	private Capability<T> holder;
	
	/**
	 * @param capabilitySupplier A method for generating capability interface instance. Will only be invoked once on construction.
	 * @param holder The corresponding {@link Capability} holder reference. This will be called when accessing the capability interface on parent objects.
	 */
	public SerializableCapabilityProvider(Supplier<T> capabilitySupplier, Capability<T> holder)
	{
		cap = capabilitySupplier.get();
		this.holder = holder;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == holder)
			return LazyOptional.of(() -> {return this.cap;}).cast();
		else return LazyOptional.empty();
	}

	@Override
	public S serializeNBT() {
		return cap.serializeNBT();
	}

	@Override
	public void deserializeNBT(S nbt) {
		cap.deserializeNBT(nbt);		
	}
}
