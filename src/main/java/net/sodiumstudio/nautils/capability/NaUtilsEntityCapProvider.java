package net.sodiumstudio.nautils.capability;

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
