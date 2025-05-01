package net.sodiumzh.nfu.capability;

import java.util.function.Supplier;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A default template Capability Provider for non-serializable capabilities.
 * For serializable caps, use {@link NFUEntitySerializableCapProvider}.
 * @param <E> Entity type using this capability
 * @param <T> Capability interface.
 */
public class NFUEntityCapProvider<E extends Entity, T> extends NFUCapProvider<T>
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
	public <U extends T> NFUEntityCapProvider(E entity, Capability<U> holder, Supplier<? extends U> capSupplier)
	{
		super(holder, capSupplier);
		this.entity = entity;
	}
	
}
