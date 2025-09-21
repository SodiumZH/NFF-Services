package net.sodiumzh.nfu.entity.vanillatrade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.sodiumzh.nfu.container.LinkableMultimap;
import net.sodiumzh.nfu.registry.NFURegistries;

import java.util.*;
 import java.util.stream.Collectors;

/**
 * A {@code VanillaTradeListingCollection} is a collection of {@link IVanillaTradeListing}s. It's the minimal unit of the
 * trade entry generator.
 * <p>It maps each merchant level to a collection of {@link IVanillaTradeListing}s
 * for the merchant level. It doesn't specify mob types or {@link VillagerProfession}s.
 * <p>{@code VanillaTradeListingCollection}s are required to be registered and accessed through registry
 * ({@link NFURegistries#VANILLA_TRADE_LISTING_COLLECTIONS}).
 * @see IVanillaTradeListing
 * @see VanillaTradeRegistry
 */
public class VanillaTradeListingCollection<T extends IVanillaTradeListing> implements IVanillaTradeListingCollection<T>
{
	/**
	 * Do not modify the table externally by reflection of mixin. It's unsafe, unless you fully understand
	 * what you're doing.
	 */
	final LinkableMultimap<Integer, T> table = new LinkableMultimap<>();

	// This reference is recorded for
	private final Set<VanillaTradeListingCollection<?>> attachedCollections = new HashSet<>();


	int helperCount = 0;

	public VanillaTradeListingCollection() {}

	public static <T extends IVanillaTradeListing> VanillaTradeListingCollection<T> empty()
	{
		return new VanillaTradeListingCollection<>();
	}

	public int getHelperCreationCount() {
		return helperCount;
	}

	public ImmutableSetMultimap<Integer, T> getTableSnapshot() {
		return table.copyAsImmutable();
	}

	@Override
	public boolean isEmpty()
	{
		return this.table.isEmpty();
	}

	public VanillaTradeListingCollection<T> add(int merchantLevel, T t)
	{
		if (t != null && t.isValid())
			table.put(merchantLevel, t);
		return this;
	}

	public VanillaTradeListingCollection<T> add(T t) {
		return add(t.getDefaultRequiredLevel(), t);
	}

	public VanillaTradeListingCollection<T> addAll(Multimap<Integer, T> c)
	{
        c.entries().stream().filter(entry -> entry.getValue().isValid())
				.forEach(entry -> table.put(entry.getKey(), entry.getValue()));
		return this;
	}

	public VanillaTradeListingCollection<T> addAll(Collection<? extends T> c) {
		c.forEach(entry -> table.put(entry.getDefaultRequiredLevel(), entry));
		return this;
	}

	public VanillaTradeListingCollection<T> attach(Multimap<Integer, T> other)
	{
		table.attach(other);
		return this;
	}
	
	public VanillaTradeListingCollection<T> attach(VanillaTradeListingCollection<? extends T> other)
	{
		table.attach(other.table);
		return this;
	}

	/**
	 * Get a subset of valid listings with the given merchant level.
	 */
	@Override
	public Set<T> forLevel(int level)
	{
		return this.table.get(level).stream().filter(IVanillaTradeListing::isValid)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Get all merchant levels that have valid listings, sorted from smallest to largest.
	 */
	@Override
	public List<Integer> allLevels()
	{
		return table.keySet().stream()
				.filter(i -> !table.get(i).stream().filter(IVanillaTradeListing::isValid).collect(Collectors.toSet()).isEmpty())
				.sorted(Comparator.comparingInt(i -> i)).toList();
	}

	@Override
	public SetMultimap<Integer, T> allLevelsAndListings() {
		List<Integer> allLevels = allLevels();
		SetMultimap<Integer, T> res = HashMultimap.create();
		allLevels.forEach(i -> res.putAll(i, table.get(i).stream().filter(IVanillaTradeListing::isValid).collect(Collectors.toSet())));
		return res;
	}
	
	@Override
	public String toString() {
		return "VanillaTradeListingCollection{\n" + this.table.copyAsImmutable().toString() + "\n}";
	}

	public Optional<ResourceLocation> getRegistryKey() {
		return Optional.ofNullable(NFURegistries.VANILLA_TRADE_LISTING_COLLECTIONS.getKey(this));
	}

	public List<IVanillaTradeListing> allListings() {
		return table.copyAsImmutable().entries().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}
}
