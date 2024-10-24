package net.sodiumzh.nff.services.entity.taming;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

/** Defines entity types before and after taming. Mobs which are registered to be a tamable type (type before
 * taming) will be automatically attached a {@link CNFFTamable} capability.
 */
public class NFFTamingMapping {

	private NFFTamingMapping()
	{
	};
	
	protected static final NFFTamingMapping REGISTRY = new NFFTamingMapping(); 
	
	protected static class Entry
	{
		public ResourceLocation fromType = null;
		public ResourceLocation convertToType = null;
		public Supplier<NFFTamingProcess> process = null;
		
		public Entry(ResourceLocation before, ResourceLocation after, Supplier<NFFTamingProcess> process)
		{
			this.fromType = before;
			this.convertToType = after;
			this.process = process;
		}

		@SuppressWarnings("unchecked")
		public EntityType<? extends Mob> getTypeBefore() {
			return (EntityType<? extends Mob>) ForgeRegistries.ENTITY_TYPES.getValue(fromType);
		}

		@SuppressWarnings("unchecked")
		public EntityType<? extends Mob> getTypeAfter() {
			return (EntityType<? extends Mob>) ForgeRegistries.ENTITY_TYPES.getValue(convertToType);
		}
	}
		
	private final ArrayList<Entry> map = new ArrayList<>();

	/* Register */
	
	public static void register(@Nonnull ResourceLocation from, @Nonnull ResourceLocation convertTo, @Nonnull Supplier<NFFTamingProcess> process, boolean override)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.fromType.equals(from))
			{
				if (override)
				{
					REGISTRY.map.remove(entry);
					break;
				}
				else
				{
					return;
				}
			}
		}
		Entry newEntry = new Entry(from, convertTo, process);
		REGISTRY.map.add(newEntry);
	}
	
	public static void register(@Nonnull ResourceLocation fromType, @Nonnull ResourceLocation convertToType, @Nonnull Supplier<NFFTamingProcess> process)
	{
		register(fromType, convertToType, process, false);
	}

	/* Search */
	
	private static Entry getEntryFromType(EntityType<? extends Mob> fromType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.getTypeBefore().equals(fromType))
			{
				return entry;
			}
		}
		return null;
	}
	
	// Get which type this mob should convert to (from type)
	public static EntityType<? extends Mob> getConvertTo(EntityType<? extends Mob> fromType)
	{
		Entry entry = getEntryFromType(fromType);
		if (entry != null)
			return entry.getTypeAfter();
		else return null;
	}
	
	// Get which type this mob should convert to (from mob)
	@SuppressWarnings("unchecked")
	public static EntityType<? extends Mob> getConvertTo(Mob fromMob)
	{
		return getConvertTo((EntityType<? extends Mob>) fromMob.getType());
	}

	/**
	 * @deprecated use {@code getProcessSupplier} or {@code getProcess} instead.
	 */
	@Deprecated
	public static NFFTamingProcess getHandler(EntityType<? extends Mob> fromType)
	{
		return getProcess(fromType);
	}

	/**
	 * // Get which taming process this mob should use (from type) as supplier.
	 * @param fromType Type of the "wild" mob.
	 * @return Process.
	 */
	public static Supplier<NFFTamingProcess> getProcessSupplier(EntityType<? extends Mob> fromType)
	{
		Entry entry = getEntryFromType(fromType);
		if (entry != null)
			return entry.process;
		else return null;
	}

	/**
	 * // Get which taming process this mob should use (from type) as instance.
	 * @param fromType Type of the "wild" mob.
	 * @return Process.
	 */
	public static NFFTamingProcess getProcess(EntityType<? extends Mob> fromType)
	{
		Supplier<NFFTamingProcess> supplier = getProcessSupplier(fromType);
		return supplier != null ? supplier.get() : null;
	}

	/**
	 * // Get which taming process this mob ("wild") should use as supplier.
	 * @param fromMob The "wild" mob.
	 * @return Process.
	 */
	@SuppressWarnings("unchecked")
	public static Supplier<NFFTamingProcess> getProcessSupplier(Mob fromMob)
	{
		return getProcessSupplier((EntityType<? extends Mob>) fromMob.getType());
	}

	/**
	 * // Get which taming process this mob ("wild") should use as instance.
	 * @param fromMob The "wild" mob.
	 * @return Process.
	 */
	@SuppressWarnings("unchecked")
	public static NFFTamingProcess getProcess(Mob fromMob)
	{
		return getProcess((EntityType<? extends Mob>) fromMob.getType());
	}

	/**
	 * @deprecated use {@code getProcessSupplier} or {@code getProcess} instead.
	 */
	@Deprecated
	public static NFFTamingProcess getHandler(Mob fromMob)
	{
		return getProcess(fromMob);
	}
	
	/** Get if the type ("wild" type) is tamable. */
	public static boolean contains(EntityType<? extends Mob> fromType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.getTypeBefore().equals(fromType))
			{
				return true;
			}
		}
		return false;
	}
	
	/** Get if the mob is tamable. */
	@SuppressWarnings("unchecked")
	public static boolean contains(Mob fromMob)
	{
		return contains((EntityType<? extends Mob>) fromMob.getType());
	}
	
	public static EntityType<? extends Mob> getTypeBefore(EntityType<? extends Mob> befriendedType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.getTypeAfter().equals(befriendedType))
			{
				return entry.getTypeBefore();
			}
		}
		throw new IllegalArgumentException("Type " + befriendedType.getDescriptionId() + "is not a nff-tamed mob.");
	}
	
	@SuppressWarnings("unchecked")
	public static EntityType<? extends Mob> getTypeBefore(Mob tamed)
	{
		EntityType<? extends Mob> tamedType = (EntityType<? extends Mob>) tamed.getType();
		
		for (Entry entry: REGISTRY.map)
		{
			if (entry.getTypeAfter().equals(tamedType))
			{
				return entry.getTypeBefore();
			}
		}
		throw new IllegalArgumentException("Type " + tamedType.getDescriptionId() + "is not a nff-tamable mob.");
	}	
	
	public static Set<EntityType<?>> getAllTamableTypes()
	{
		Set<EntityType<?>> types = new HashSet<EntityType<?>>();
		for (Entry entry: REGISTRY.map)
		{
			types.add(entry.getTypeBefore());
		}
		return types;
	}
	
	public static Set<EntityType<?>> getAllTamedTypes()
	{
		Set<EntityType<?>> types = new HashSet<EntityType<?>>();
		for (Entry entry: REGISTRY.map)
		{
			types.add(entry.getTypeAfter());
		}
		return types;
	}
}
