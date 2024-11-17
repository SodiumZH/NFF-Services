package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.entity.MobApplicableItemTable;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeListing;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeListings;
import net.sodiumzh.nautils.entity.vanillatrade.VanillaTradeRegistry;
import net.sodiumzh.nautils.network.NaUtilsDataSerializer;

import java.util.function.Function;

public class NaUtilsRegistries {

    // Just for loading this class on init
    public static void init(){}
    public static final NaUtilsRegistry<NaUtilsDataSerializer<?>> DATA_SERIALIZERS =
            new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "data_serializers"));
    public static final NaUtilsRegistry<MobApplicableItemTable> MOB_APPLICABLE_ITEM_TABLES =
            new NaUtilsRegistry<MobApplicableItemTable>(new ResourceLocation(NaUtils.MOD_ID, "mob_applicable_item_tables"))
                    .setShouldGenerateOnServerSetup();
    public static final NaUtilsRegistry<Function<?, ?>> FUNCTIONS =
            new NaUtilsRegistry<>(new ResourceLocation(NaUtils.MOD_ID, "functions"));
    public static final NaUtilsRegistry<VanillaTradeRegistry> VANILLA_TRADE_REGISTRIES =
            new NaUtilsRegistry<VanillaTradeRegistry>(new ResourceLocation(NaUtils.MOD_ID, "vanilla_trade_registries"))
                    .setShouldGenerateOnServerSetup();
    public static final NaUtilsRegistry<VanillaTradeListing> VANILLA_TRADE_LISTINGS =
        new NaUtilsRegistry<VanillaTradeListing>(new ResourceLocation(NaUtils.MOD_ID, "vanilla_trade_listings"))
            .setShouldGenerateOnServerSetup();

}
