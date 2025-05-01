package net.sodiumzh.nfu.capability;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

/**
 * A capability provider without side.
 */
public class NFUCapProvider<T> implements ICapabilityProvider
{
	private T cap;
	private Capability<? extends T> holder;

	/**
	 * @param holder The corresponding {@link Capability} holder reference. This will be called when accessing the capability interface on parent objects.
	 * @param capSupplier A method for generating capability interface instance. Will only be invoked once on construction.
	 */
	public <U extends T> NFUCapProvider(Capability<U> holder, Supplier<? extends U> capSupplier)
	{
		this.holder = holder;
		this.cap = capSupplier.get();
	}
	
	@Override
	@Nonnull
	public <C> LazyOptional<C> getCapability(@Nonnull final Capability<C> cap, final @Nullable Direction side){
		if (cap == holder)
			return LazyOptional.of(() -> {return this.cap;}).cast();
		else return LazyOptional.empty();
	}
	
	protected T getCapInstance()
	{
		return cap;
	}

}
