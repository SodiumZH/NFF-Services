package net.sodiumzh.nautils.entity.vanillatrade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractVanillaTradeRegistry<T extends IVanillaTradeListing>
{
	private Map<ResourceLocation, Map<VillagerProfession, VanillaTradeListings<T>>> table = new HashMap<>();

	public AbstractVanillaTradeRegistry() {}
	
	// Getters
	
	protected Map<ResourceLocation, Map<VillagerProfession, VanillaTradeListings<T>>> getRaw()
	{
		return this.table;
	}
	
	@Nullable
	public Map<VillagerProfession, VanillaTradeListings<T>> getAllListings(ResourceLocation key)
	{
		if (key == null) return null;
		return table.get(key);
	}

	public Map<VillagerProfession, VanillaTradeListings<T>> getAllListings(EntityType<?> ofType)
	{
		if (ofType == null) return new HashMap<>();
		return Optional.ofNullable(getAllListings(ForgeRegistries.ENTITIES.getKey(ofType))).orElseGet(HashMap::new);
	}

	public VanillaTradeListings<T> getListings(ResourceLocation key, @Nullable VillagerProfession profession)
	{
		VillagerProfession profNonnull = profession == null ? VillagerProfession.NONE : profession;
		var allListings = this.getAllListings(key);
		if (allListings == null) return VanillaTradeListings.empty();
		return Optional.ofNullable(allListings.get(profNonnull)).orElseGet(VanillaTradeListings::empty);
	}

	public VanillaTradeListings<T> getListings(EntityType<?> ofType, @Nullable VillagerProfession profession)
	{
		if (ofType == null) return VanillaTradeListings.empty();
		return this.getListings(ForgeRegistries.ENTITIES.getKey(ofType), profession);
	}

	public VanillaTradeListings<T> getListings(ResourceLocation key)
	{
		return this.getListings(key, null);
	}

	public VanillaTradeListings<T> getListings(EntityType<?> ofType)
	{
		return this.getListings(ofType, null);
	}
	
	/**
	 * Check if the listings exists for a given key and profession.
	 */
	public boolean hasListings(ResourceLocation key, @Nullable VillagerProfession prof)
	{
		VillagerProfession profNonnull = prof == null ? VillagerProfession.NONE : prof;
		return this.table.containsKey(key)
			&& this.table.get(key).containsKey(profNonnull)
			&& !this.table.get(key).get(profNonnull).isEmpty();
	}
	
	/**
	 * Check if the listings exists for a given key and profession.
	 */
	public boolean hasListings(EntityType<?> type, @Nullable VillagerProfession prof)
	{
		return this.hasListings(ForgeRegistries.ENTITIES.getKey(type), prof);
	}
	
	public void putIfAbsent(ResourceLocation key, @Nullable VillagerProfession prof)
	{
		VillagerProfession profNonnull = prof == null ? VillagerProfession.NONE : prof;
		if (!this.table.containsKey(key))
			this.table.put(key, new HashMap<>());
		if (!this.table.get(key).containsKey(profNonnull))
			this.table.get(key).put(profNonnull, new VanillaTradeListings<>());
	}
	
	public void putIfAbsent(ResourceLocation key)
	{
		this.putIfAbsent(key, null);
	}

	public void putListing(ResourceLocation key, @Nullable VillagerProfession prof, T listing)
	{
		this.putIfAbsent(key, prof);
		this.table.get(key).get(prof).add(listing);
	}
	
}