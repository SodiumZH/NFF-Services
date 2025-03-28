package net.sodiumzh.nautils.entity.vanillatrade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.sodiumzh.nautils.containers.LinkableMultimap;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;

import javax.annotation.Nonnull;
 import javax.annotation.Nullable;
 import java.util.*;
 import java.util.stream.Collectors;

/**
 * A {@code VanillaTradeListingCollection} is a collection of {@link IVanillaTradeListing}s. It's the minimal unit of the
 * trade entry generator.
 * <p>It maps each merchant level to a collection of {@link IVanillaTradeListing}s
 * for the merchant level. It doesn't specify mob types or {@link VillagerProfession}s.
 * <p>{@code VanillaTradeListingCollection}s are required to be registered and accessed through registry
 * ({@link NaUtilsRegistries#VANILLA_TRADE_LISTING_COLLECTIONS}).
 * @see IVanillaTradeListing
 * @see VanillaTradeRegistry
 */
public class VanillaTradeListingCollection<T extends IVanillaTradeListing>
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
	
	public VanillaTradeListingCollection<T> addAll(Multimap<Integer, T> c)
	{
        c.entries().stream().filter(entry -> entry.getValue().isValid())
				.forEach(entry -> table.put(entry.getKey(), entry.getValue()));
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
	 * Get a set of all valid listings.
	 */
	public Set<T> getValidListings()
	{
		return this.table.values().stream().filter(IVanillaTradeListing::isValid)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Get a subset of valid listings with the given merchant level.
	 */
	public Set<T> forLevel(int level)
	{
		return this.table.get(level).stream().filter(IVanillaTradeListing::isValid)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Get all merchant levels that have valid listings, sorted from smallest to largest.
	 */
	public List<Integer> allLevels()
	{
		return table.keySet().stream()
				.filter(i -> !table.get(i).stream().filter(IVanillaTradeListing::isValid).collect(Collectors.toSet()).isEmpty())
				.toList();
	}

	public SetMultimap<Integer, T> allLevelsAndListings() {
		SetMultimap<Integer, T> res = HashMultimap.create();
		table.keySet().forEach(k -> res.putAll(k, table.get(k)));
		return res;
	}

	/**
	 * Randomly pick several listings from the set with given amount and merchant level.
	 * <p>Note: the output set size could possibly be smaller than the input.
	 */ 
	public Set<T> pickListings(int amount, int merchantLevel)
	{
		return NaUtilsContainerStatics.getWeightedRandomSubset(table.get(merchantLevel).stream()
				.filter(IVanillaTradeListing::isValid)
				.collect(Collectors.toMap(t -> t, IVanillaTradeListing::getSelectionWeight)), amount);
	}
	
	/**
	 * Pick listing instances for all present levels.
	 * @param amountForEachLevel How many Listing instances it should pick for each level.
	 * Null input or absent level value will be picked 1 instance. To skip a certain level,
	 * explicitly specify it to 0.
	 * @return A Multimap of picked listing instances.
	 */
	public Multimap<Integer, T> pickListingsForAllLevels(@Nullable Map<Integer, Integer> amountForEachLevel)
	{
		Map<Integer, Integer> actualAmounts = this.allLevels().stream()
				.collect(Collectors.toMap(i -> i, i -> 1));
		if (amountForEachLevel == null) {
			this.allLevels().forEach(i -> {
				if (amountForEachLevel.containsKey(i))
					actualAmounts.put(i, amountForEachLevel.get(i));
			});
		}
		Multimap<Integer, T> res = HashMultimap.create();
		actualAmounts.entrySet().stream().map(entry -> new Tuple<>(entry.getKey(), this.pickListings(actualAmounts.get(entry.getValue()), entry.getKey())))
				.forEach(e -> res.putAll(e.getA(), e.getB()));
		return res;
	}
	
	/**
	 * Pick listing instances for all present levels.
	 * @param amountForEachLevel How many Listing instances it should pick for each level.
	 * input[i] for level i+1. Missing levels will be 1.
	 * @return A Multimap of picked listing instances.
	 */
	public Multimap<Integer, T> pickListingsForAllLevels(int... amountForEachLevel)
	{
		Map<Integer, Integer> in = new HashMap<>();
		for (int i = 0; i < amountForEachLevel.length; ++i)
			in.put(i + 1, amountForEachLevel[i]);
		return pickListingsForAllLevels(in);
	}
	
	/**
	 * Pick listing instances for all specified levels in the input map keys.
	 * @param amountForEachLevel How many Listing instances it should pick for each level.
	 * Missing levels will be skipped.
	 * @return A Multimap of picked listing instances.
	 */
	public Multimap<Integer, T> pickListingForSpecifiedLevels(@Nonnull Map<Integer, Integer> amountForEachLevel)
	{
		Map<Integer, Integer> actualAmounts = new HashMap<>(amountForEachLevel);
		this.allLevels().forEach(i -> {
			if (!amountForEachLevel.containsKey(i))
				actualAmounts.put(i, 0);
		});
		actualAmounts.keySet().removeIf(i -> !this.allLevels().contains(i));
		return this.pickListingsForAllLevels(actualAmounts);

	}
	
	/**
	 * Pick listing instances for levels from 1 to input length.
	 * @param amountForEachLevel How many Listing instances it should pick for each level.
	 * input[i] for level i+1.
	 * @return A list of Listing instances with ascending order in level. 
	 */
	public Multimap<Integer, T> pickListingForSpecifiedLevels(int... amountForEachLevel)
	{
		Map<Integer, Integer> actualAmounts = new HashMap<>();
		for (int i = 0; i < amountForEachLevel.length; ++i) {
			actualAmounts.put(i + 1, amountForEachLevel[i]);
		}
		return this.pickListingForSpecifiedLevels(actualAmounts);
	}
	
	@Override
	public String toString() {
		return "VanillaTradeListingCollection{\n" + this.table.copyAsImmutable().toString() + "\n}";
	}

	public static void getFromJsons(VanillaTradeListingCollection<VanillaTradeListing> in, Collection<JsonElement> jsons) {

	}


}
