package net.sodiumzh.nfu.entity.vanillatrade;

import net.minecraftforge.eventbus.api.Event;

/**
 * Posted when the FIRST {@link VanillaTradeListingCollectionHelper} is created for a {@link VanillaTradeListingCollection},
 * allowing to modify the collection externally.
 */
public class VanillaTradeListingCollectionHelperEvent extends Event {

    private final VanillaTradeListingCollectionHelper helper;
    private final VanillaTradeListingCollection<? extends VanillaTradeListing> collection;

    public VanillaTradeListingCollectionHelperEvent(VanillaTradeListingCollectionHelper helper, VanillaTradeListingCollection<? extends VanillaTradeListing> collection) {
        this.helper = helper;
        this.collection = collection;
    }

    public VanillaTradeListingCollectionHelper getHelper() {
        return helper;
    }

    public VanillaTradeListingCollection<? extends VanillaTradeListing> getCollection() {
        return collection;
    }

}
