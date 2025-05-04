package net.sodiumzh.nfu.entity.vanillatrade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.sodiumzh.nfu.util.NFUContainerStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@code IVanillaTradeListingCollection} is the minimal unit of the trade entry generator. It maps
 * each merchant level to a collection of {@link IVanillaTradeListing}s
 * for the merchant level. It doesn't specify mob types or {@link VillagerProfession}s.
 * <p>This interface is only for queries. For modifiable collection, use {@link VanillaTradeListingCollection}.
 *
 * @see IVanillaTradeListing
 */
public interface IVanillaTradeListingCollection<T extends IVanillaTradeListing>
{

	public boolean isEmpty();

	/**
	 * Get a subset of valid listings with the given merchant level.
	 */
	public Set<T> forLevel(int level);
	
	/**
	 * Get all merchant levels that have valid listings, sorted from smallest to largest.
	 */
	public List<Integer> allLevels();

	/**
	 * Get all merchant levels that have valid listings, and the corresponding listings.
	 */
	public SetMultimap<Integer, T> allLevelsAndListings();

	/**
	 * Randomly pick several listings from the set with given amount and merchant level.
	 * <p>Note: the output set size could possibly be smaller than the input.
	 */ 
	public default Set<T> pickListings(int amount, int merchantLevel)
	{
		return NFUContainerStatics.getWeightedRandomSubset(allLevelsAndListings().get(merchantLevel).stream()
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
	public default Multimap<Integer, T> pickListingsForAllLevels(@Nullable Map<Integer, Integer> amountForEachLevel)
	{
		Map<Integer, Integer> actualAmounts = this.allLevels().stream()
				.collect(Collectors.toMap(i -> i, i -> 1));
		if (amountForEachLevel != null) {
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
	public default Multimap<Integer, T> pickListingsForAllLevels(int... amountForEachLevel)
	{
		Map<Integer, Integer> in = NFUContainerStatics.toIndexMapInt(amountForEachLevel)
				.entrySet().stream().collect(Collectors.toMap(i -> i.getKey() + 1, Map.Entry::getValue));
		return pickListingsForAllLevels(in);
	}
	
	/**
	 * Pick listing instances for all specified levels in the input map keys.
	 * @param amountForEachLevel How many Listing instances it should pick for each level.
	 * Missing levels will be skipped.
	 * @return A Multimap of picked listing instances.
	 */
	public default Multimap<Integer, T> pickListingForSpecifiedLevels(@Nonnull Map<Integer, Integer> amountForEachLevel)
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
	public default Multimap<Integer, T> pickListingForSpecifiedLevels(int... amountForEachLevel)
	{
		Map<Integer, Integer> actualAmounts = new HashMap<>();
		for (int i = 0; i < amountForEachLevel.length; ++i) {
			actualAmounts.put(i + 1, amountForEachLevel[i]);
		}
		return this.pickListingForSpecifiedLevels(actualAmounts);
	}

}
