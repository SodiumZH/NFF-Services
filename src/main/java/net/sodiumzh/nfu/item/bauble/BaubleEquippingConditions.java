package net.sodiumzh.nfu.item.bauble;

import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

public class BaubleEquippingConditions {

    public static final NFURegistryEntryCollection<BaubleEquippingCondition> CONDITION_REGISTRY_COLLECTION
        = NFURegistryEntryCollection.create(NFUBaubleAPI.EQUIPPING_CONDITIONS, NFULibrary.MOD_ID);

    public static final NFURegistry.Accessor<BaubleEquippingCondition> CONDITION_ALWAYS =
        CONDITION_REGISTRY_COLLECTION.register("always", () -> BaubleEquippingCondition.of(args -> true).setName("always"));

    public static final NFURegistry.Accessor<BaubleEquippingCondition> CONDITION_NEVER =
        CONDITION_REGISTRY_COLLECTION.register("never", () -> BaubleEquippingCondition.of(args -> false).setName("never"));

}
