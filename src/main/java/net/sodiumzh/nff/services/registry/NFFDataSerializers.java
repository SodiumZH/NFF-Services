package net.sodiumzh.nff.services.registry;

import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nfu.network.NFUDataSerializer;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

public class NFFDataSerializers {

    public static final NFURegistryEntryCollection<NFUDataSerializer<?>> COLLECTION =
        NFURegistryEntryCollection.create(NFURegistries.DATA_SERIALIZERS, NFFServices.MOD_ID);

    public static final NFURegistry.Accessor<NFUDataSerializer<NFFTamedMobInventory>> TAMED_MOB_INVENTORY =
        COLLECTION.register("tamed_mob_inventory", () -> NFUDataSerializer.create(NFFTamedMobInventory.class,
            (buf, in) -> in.writeBuf(buf),
            NFFTamedMobInventory::fromBuf,
            NFFTamedMobInventory::toTag,
            NFFTamedMobInventory::makeFromTag));

}
