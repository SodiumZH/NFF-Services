package net.sodiumzh.nff.services.entity.taming;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

// Define which type it will convert to after befriending for each befriendable mob
public class NFFTamingMapping {

	private NFFTamingMapping()
	{
	};
	
	protected static final NFFTamingMapping REGISTRY = new NFFTamingMapping(); 
	
	protected static class Entry
	{
		public EntityType<? extends Mob> fromType = null;
		public EntityType<? extends Mob> convertToType = null;
		public NFFTamingProcess handler = null;
		
		public Entry(EntityType<? extends Mob> before, EntityType<? extends Mob> after, NFFTamingProcess handler)
		{
			this.fromType = before;
			this.convertToType = after;
			this.handler = handler;
		}
	}
		
	private ArrayList<Entry> map = new ArrayList<Entry>();

	/* Register */
	
	public static void register(@Nonnull EntityType<? extends Mob> from, @Nonnull EntityType<? extends Mob> convertTo, @Nonnull NFFTamingProcess handler, boolean override)
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
		Entry newEntry = new Entry(from, convertTo, handler);
		REGISTRY.map.add(newEntry);
	}
	
	public static void register(@Nonnull EntityType<? extends Mob> fromType, @Nonnull EntityType<? extends Mob> convertToType, @Nonnull NFFTamingProcess handler)
	{
		register(fromType, convertToType, handler, false);
	}

	/* Search */
	
	private static Entry getEntryFromType(EntityType<? extends Mob> fromType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.fromType.equals(fromType))
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
			return entry.convertToType;
		else return null;
	}
	
	// Get which type this mob should convert to (from mob)
	@SuppressWarnings("unchecked")
	public static EntityType<? extends Mob> getConvertTo(Mob fromMob)
	{
		return getConvertTo((EntityType<? extends Mob>) fromMob.getType());
	}
	
	// Get which befriending handler this mob should use (from type)
	public static NFFTamingProcess getHandler(EntityType<? extends Mob> fromType)
	{
		Entry entry = getEntryFromType(fromType);
		if (entry != null)
			return entry.handler;
		else return null;
	}
	
	// Get which befriending handler this mob should use (from type)
	@SuppressWarnings("unchecked")
	public static NFFTamingProcess getHandler(Mob fromMob)
	{
		return getHandler((EntityType<? extends Mob>) fromMob.getType());
	}
	
	// Get if the type is befriendable (from type)
	public static boolean contains(EntityType<? extends Mob> fromType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.fromType.equals(fromType))
			{
				return true;
			}
		}
		return false;
	}
	
	// Get if the type is befriendable (from mob)
	@SuppressWarnings("unchecked")
	public static boolean contains(Mob fromMob)
	{
		return contains((EntityType<? extends Mob>) fromMob.getType());
	}
	
	public static EntityType<? extends Mob> getTypeBefore(EntityType<? extends Mob> befriendedType)
	{
		for (Entry entry: REGISTRY.map)
		{
			if (entry.convertToType.equals(befriendedType))
			{
				return entry.fromType;
			}
		}
		throw new IllegalArgumentException("Type " + befriendedType.getDescriptionId() + "is not a befriended mob.");
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static EntityType<? extends Mob> getTypeBefore(Mob befriendedMob)
	{
		EntityType<? extends Mob> befriendedType = (EntityType<? extends Mob>) befriendedMob.getType();
		
		for (Entry entry: REGISTRY.map)
		{
			if (entry.convertToType.equals(befriendedType))
			{
				return entry.fromType;
			}
		}
		return null;
	}	
	
	public static Set<EntityType<?>> getAllBefriendableTypes()
	{
		Set<EntityType<?>> types = new HashSet<EntityType<?>>();
		for (Entry entry: REGISTRY.map)
		{
			types.add(entry.fromType);
		}
		return types;
	}
	
	public static Set<EntityType<?>> getAllBefriendedTypes()
	{
		Set<EntityType<?>> types = new HashSet<EntityType<?>>();
		for (Entry entry: REGISTRY.map)
		{
			types.add(entry.convertToType);
		}
		return types;
	}
}
