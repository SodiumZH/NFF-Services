package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.entity.EntityAttributeProvider;
import net.sodiumzh.nautils.entity.MobApplicableItemTable;
import net.sodiumzh.nautils.entity.anger.MobAngerReason;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeListing;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeListings;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeRegistry;
import net.sodiumzh.nautils.network.NaUtilsDataSerializer;

import java.util.function.Function;

public class NaUtilsRegistries {

    // Just for loading this class on init
    public static void init(){}

    /**
     * Registry for NaUtils data serializers. It's a handler for data that can be encoded/decoded both between NBT
     * and between byte buffer.
     */
    public static final NaUtilsRegistry<NaUtilsDataSerializer<?>> DATA_SERIALIZERS =
            new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "data_serializers"));

    /**
     * Registry for {@link MobApplicableItemTable}s. This is a table to define a set of actions in which an item is applied
     * to a mob, and the consequence of the actions.
     */
    public static final NaUtilsRegistry<MobApplicableItemTable> MOB_APPLICABLE_ITEM_TABLES =
            new NaUtilsRegistry<MobApplicableItemTable>(new ResourceLocation(NaUtils.MOD_ID, "mob_applicable_item_tables"))
                    .setShouldGenerateOnServerSetup();

    /**
     * Registry for generic {@link Function}s. Note that the functions' input/output types are generic, and will not do
     * any type check before running. Ensure the types match before running.
     */
    public static final NaUtilsRegistry<Function<?, ?>> FUNCTIONS =
            new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "functions"));

    /**
     * Registry for trade registries (from vanilla trade system). A trade registry is a set of trade listings.
     */
    public static final NaUtilsRegistry<VanillaTradeRegistry> VANILLA_TRADE_REGISTRIES =
            new NaUtilsRegistry<VanillaTradeRegistry>(new ResourceLocation(NaUtils.MOD_ID, "vanilla_trade_registries"))
                    .setShouldGenerateOnServerSetup();

    /**
     * Registry for trade listings (from vanilla trade system). A trade listing is a generator for providing random trade offers
     * for mobs.
     */
    public static final NaUtilsRegistry<VanillaTradeListing> VANILLA_TRADE_LISTINGS =
        new NaUtilsRegistry<VanillaTradeListing>(new ResourceLocation(NaUtils.MOD_ID, "vanilla_trade_listings"))
            .setShouldGenerateOnServerSetup();

    public static final NaUtilsRegistry<MobAngerReason> MOB_ANGER_REASONS =
        new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "mob_anger_reasons"));


    public static final NaUtilsRegistry<MobAngerRules> MOB_ANGER_RULES =
        new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "mob_anger_rules"));

    public static final NaUtilsRegistry<EntityAttributeProvider> ENTITY_ATTRIBUTE_PROVIDERS =
            new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "entity_anger_rules"));

}
