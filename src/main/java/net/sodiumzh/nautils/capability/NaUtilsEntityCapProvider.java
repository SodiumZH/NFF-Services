package net.sodiumzh.nautils.capability;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A default template Capability Provider for non-serializable capabilities.
 * For serializable caps, use {@link NaUtilsEntitySerializableCapProvider}.
 * @param <E> Entity type using this capability
 * @param <T> Capability interface.
 */
public class NaUtilsEntityCapProvider<E extends Entity, T> extends NaUtilsCapProvider<T>
{
	private E entity;
	private T cap;
	private Capability<? extends T> holder;
	
	public E getEntity()
	{
		return entity;
	}
	
	/**
	 * @param entity Entity owning this capability.
	 * @param holder The corresponding {@link Capability} holder reference. This will be called when accessing the capability interface on parent objects.
	 * @param capSupplier A method for generating capability interface instance. Will only be invoked once on construction.
	 */
	public <U extends T> NaUtilsEntityCapProvider(E entity, Capability<U> holder, Supplier<U> capSupplier)
	{
		super(holder, capSupplier);
		this.entity = entity;
	}
	
}
