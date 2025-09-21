package net.sodiumzh.nfu.registry;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.entity.EntityAttributeProvider;
import net.sodiumzh.nfu.entity.MobApplicableItemTable;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.entity.vanillatrade.VanillaTradeListing;
import net.sodiumzh.nfu.entity.vanillatrade.VanillaTradeListingCollection;
import net.sodiumzh.nfu.entity.vanillatrade.VanillaTradeRegistry;
import net.sodiumzh.nfu.function.RegistrablePredicate;
import net.sodiumzh.nfu.item.bauble.IBaubleRegistryEntry;
import net.sodiumzh.nfu.math.IFieldPattern3D;
import net.sodiumzh.nfu.math.IInequalityPattern3D;
import net.sodiumzh.nfu.network.NFUDataSerializer;

import java.util.function.Function;

public class NFURegistries {

    // Just for loading this class on init
    public static void init(){}

    /**
     * Registry for NaUtils data serializers. It's a handler for data that can be encoded/decoded both between NBT
     * and between byte buffer.
     */
    public static final NFURegistry<NFUDataSerializer<?>> DATA_SERIALIZERS =
            new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "data_serializers"));

    /**
     * Registry for {@link MobApplicableItemTable}s. This is a table to define a set of actions in which an item is applied
     * to a mob, and the consequence of the actions.
     */
    public static final NFURegistry<MobApplicableItemTable> MOB_APPLICABLE_ITEM_TABLES =
            new NFURegistry<MobApplicableItemTable>(new ResourceLocation(NFULibrary.MOD_ID, "mob_applicable_item_tables"))
                    .setShouldGenerateOnServerSetup()
                    .setUnavailableBefore(NFURegistry.SetupPhase.SERVER_SETUP)
                    .setSide(NFURegistry.AvailableSide.SERVER);

    /**
     * Registry for generic {@link Function}s. Note that the functions' input/output types are generic, and will not do
     * any type check before running. Ensure the types match before running.
     */
    public static final NFURegistry<Function<?, ?>> FUNCTIONS =
            new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "functions"));

    public static final NFURegistry<RegistrablePredicate<?>> PREDICATES =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "predicates"));

    /**
     * Registry for trade listing (from vanilla trade system). A trade listing is a generator for providing random trade offers
     * for mobs.
     */
    public static final NFURegistry<VanillaTradeListing> VANILLA_TRADE_LISTINGS =
        new NFURegistry<VanillaTradeListing>(new ResourceLocation(NFULibrary.MOD_ID, "vanilla_trade_listings"))
                .setShouldGenerateOnServerSetup()
                .setUnavailableBefore(NFURegistry.SetupPhase.SERVER_SETUP)
                .setSide(NFURegistry.AvailableSide.SERVER);

    public static final NFURegistry<VanillaTradeListingCollection<?>> VANILLA_TRADE_LISTING_COLLECTIONS =
            new NFURegistry<VanillaTradeListingCollection<?>>(new ResourceLocation(NFULibrary.MOD_ID, "vanilla_trade_listing_collections"))
                .setShouldGenerateOnServerSetup()
                .setUnavailableBefore(NFURegistry.SetupPhase.SERVER_SETUP)
                .setSide(NFURegistry.AvailableSide.SERVER)
                .setLoadAfter(VANILLA_TRADE_LISTINGS);

    /**
     * Registry for trade registries (from vanilla trade system). A trade registry is a set of trade collections.
     */
    public static final NFURegistry<VanillaTradeRegistry> VANILLA_TRADE_REGISTRIES =
            new NFURegistry<VanillaTradeRegistry>(new ResourceLocation(NFULibrary.MOD_ID, "vanilla_trade_registries"))
                    .setShouldGenerateOnServerSetup()
                    .setUnavailableBefore(NFURegistry.SetupPhase.SERVER_SETUP)
                    .setSide(NFURegistry.AvailableSide.SERVER)
                    .setLoadAfter(VANILLA_TRADE_LISTINGS)
                    .setLoadAfter(VANILLA_TRADE_LISTING_COLLECTIONS);

    public static final NFURegistry<MobAngerReason> MOB_ANGER_REASONS =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "mob_anger_reasons"));

    public static final NFURegistry<MobAngerRules> MOB_ANGER_RULES =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "mob_anger_rules"));

    public static final NFURegistry<EntityAttributeProvider> ENTITY_ATTRIBUTE_PROVIDERS =
            new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "entity_attribute_providers"));

    public static final NFURegistry<IBaubleRegistryEntry> BAUBLES =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "baubles"));

    /**
     * A shortcut to {@link IInequalityPattern3D#REGISTRY}.
     */
    public static final NFURegistry<IInequalityPattern3D> INEQUALITY_PATTERNS = IInequalityPattern3D.REGISTRY;

    /**
     * A shortcut to {@link IFieldPattern3D#REGISTRY}.
     */
    public static final NFURegistry<IFieldPattern3D> FIELD_PATTERNS = IFieldPattern3D.REGISTRY;

}
