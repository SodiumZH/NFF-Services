package net.sodiumstudio.nautils.capability;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A capability provider without side.
 */
public class NaUtilsCapProvider<T> implements ICapabilityProvider
{
	private T cap;
	private Capability<T> holder;

	/**
	 * @param entity Entity owning this capability.
	 * @param holder The corresponding {@link Capability} holder reference. This will be called when accessing the capability interface on parent objects.
	 * @param capSupplier A method for generating capability interface instance. Will only be invoked once on construction.
	 */
	public NaUtilsCapProvider(Capability<T> holder, Supplier<T> capSupplier)
	{
		this.holder = holder;
		this.cap = capSupplier.get();
	}
	
	@Override
	@NotNull
	public <C> LazyOptional<C> getCapability(@NotNull final Capability<C> cap, final @Nullable Direction side){
		if (cap == holder)
			return LazyOptional.of(() -> {return this.cap;}).cast();
		else return LazyOptional.empty();
	}
	
	protected T getCapInstance()
	{
		return cap;
	}

}
