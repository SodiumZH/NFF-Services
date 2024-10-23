package net.sodiumzh.nautils.registries;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.data.ServerSideRegistry;
import net.sodiumzh.nautils.entity.ItemApplyingToMobTable;
import net.sodiumzh.nautils.network.NaUtilsDataSerializer;

import java.util.function.Function;

public class NaUtilsRegistries {

    // Just for loading this class on init
    public static void init(){}
    public static final NaUtilsRegistry<NaUtilsDataSerializer<?>> DATA_SERIALIZERS =
            new NaUtilsRegistry<>(NaUtilsDataSerializer.class, new ResourceLocation(NaUtils.MOD_ID, "data_serializers"));

    public static final NaUtilsRegistry<ItemApplyingToMobTable> ITEM_APPLYING_TO_MOB_TABLES =
            new NaUtilsRegistry<ItemApplyingToMobTable>(ItemApplyingToMobTable.class, new ResourceLocation(NaUtils.MOD_ID, "item_applying_to_mob_tables"))
                    .setShouldGenerateOnSetup();

    public static final NaUtilsRegistry<Function<?, ?>> FUNCTIONS =
            new NaUtilsRegistry<>(Function.class, new ResourceLocation(NaUtils.MOD_ID, "functions"));

}
