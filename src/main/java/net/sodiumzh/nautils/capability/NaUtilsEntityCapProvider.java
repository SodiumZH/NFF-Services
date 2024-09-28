package net.sodiumzh.nautils.capability;

import java.util.function.Supplier;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;

/**
 * A default template {@link CapabilityProvider} for serializable capabilities.
 * @param <E>
 * @param <T>
 */
public class NaUtilsEntityCapProvider<E extends Entity, T> extends NaUtilsCapProvider<T>
{
	private E entity;
	private T cap;
	private Capability<T> holder;
	
	public E getEntity()
	{
		return entity;
	}
	
	/**
	 * @param entity Entity owning this capability.
	 * @param holder The corresponding {@link Capability} holder reference. This will be called when accessing the capability interface on parent objects.
	 * @param capSupplier A method for generating capability interface instance. Will only be invoked once on construction.
	 */
	public NaUtilsEntityCapProvider(E entity, Capability<T> holder, Supplier<T> capSupplier)
	{
		super(holder, capSupplier);
		this.entity = entity;
	}
	
}
