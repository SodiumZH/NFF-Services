package net.sodiumzh.nfu.entity.vanillatrade;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.sodiumzh.nfu.registry.NFURegistries;

/**
 * NFU' extension of vanilla {@link VillagerTrades.ItemListing}.
 * <p>A {@link VillagerTrades.ItemListing} represents a randomizable trade entry generator that can be picked and generate
 * a trade entry ({@link MerchantOffer}) for a vanilla trade list. The listings will not be stored in trade-able mob instances,
 * but in registries. When generating a mob's trade list, the game randomly picks {@link VillagerTrades.ItemListing}s from
 * the corresponding registries, generates an entry from each {@link VillagerTrades.ItemListing} as a {@link MerchantOffer},
 * and stores them into the mob instance. Usually, each mob instance stores a collection of {@link MerchantOffer} as
 * {@link MerchantOffers}, and {@link VillagerTrades.ItemListing}s are only read to generate the offers on the trade initialization.
 * <p>In NFU Vanilla Trade API, the {@link VillagerTrades.ItemListing}s (as {@link IVanillaTradeListing}s) are defined
 * and registered in {@link VanillaTradeListingCollection}s, which is a mapping from each merchant level to a set of
 * {@link IVanillaTradeListing}s for this level. {@link VanillaTradeListingCollection}s are not mob-specific, and should be
 * registered in {@link NFURegistries#VANILLA_TRADE_LISTING_COLLECTIONS} on mod initialization.
 * @see VanillaTradeListingCollection
 */
public interface IVanillaTradeListing extends VillagerTrades.ItemListing
{

	/**
	 * Get a double value representing the "selection weight". That is, the higher value meaning a higher probability of being chosen
	 * when generating random {@link MerchantOffers} from a set of listings.
	 */
	public double getSelectionWeight();
	
	/**
	 * Check if the listing is valid i.e. can provide a valid {@code MerchantOffer}.
	 */
	public boolean isValid();

	/**
	 * Default required level. This will be used when omitting the level parameter
	 * on adding to the listing collection.
	 */
	public int getDefaultRequiredLevel();
}
